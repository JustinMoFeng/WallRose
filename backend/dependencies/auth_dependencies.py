from passlib.context import CryptContext
from datetime import datetime, timedelta
from typing import Optional
from jose import jwt
from fastapi import Depends, HTTPException, status
import os
from fastapi.security import OAuth2PasswordBearer
from models.user_models import UserInDB, TokenData
from jose import JWTError,ExpiredSignatureError
from database import get_user_collection
from dotenv import load_dotenv

load_dotenv()

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="login")

# 密码上下文
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

hashed_password = pwd_context.hash("example_password")
assert pwd_context.verify("example_password", hashed_password)

def hash_password(password: str):
    return pwd_context.hash(password)

def verify_password(plain_password, hashed_password):
    return pwd_context.verify(plain_password, hashed_password)

def create_access_token(data: dict, expires_delta: Optional[timedelta] = None):
    # 生成token的函数
    to_encode = data.copy()
    if expires_delta:
        expire = datetime.utcnow() + expires_delta
    else:
        # 默认过期时间为15分钟
        expire = datetime.utcnow() + timedelta(minutes=15)
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(to_encode, os.getenv("SECRET_KEY"), algorithm=os.getenv("ALGORITHM"))
    return encoded_jwt

def parse_token(token: str = Depends(oauth2_scheme)):
    token_data = jwt.decode(token, os.getenv("SECRET_KEY"), algorithms=[os.getenv("ALGORITHM")])
    return token_data

async def get_user(username: str):
    collection = get_user_collection()
    user_data = await collection.find_one({"username": username})
    if user_data:
        user = UserInDB(**user_data)
        return user
    else:
        return None
    
async def authenticate_user(username: str, password: str):
    # 从mongodb中查询用户信息
    user = await get_user(username)
    if user is None:
        return False
    if not verify_password(password, user.hashed_password):
        return False
    return user

async def get_current_user(token: str = Depends(oauth2_scheme)):
    credentials_exception = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="token失效，请重新登陆",
        headers={"WWW-Authenticate": "Bearer"},
    )
    try:
        payload = jwt.decode(token, os.getenv("SECRET_KEY"), algorithms=[os.getenv("ALGORITHM")])
        username: str = payload.get("username")
        if username is None:
            raise credentials_exception
        token_data = TokenData(username=username,user_id=payload.get("user_id"))
    except ExpiredSignatureError:
        raise credentials_exception
    except JWTError:
        raise credentials_exception
    user = await get_user(token_data.username)
    if user is None:
        raise credentials_exception
    return user
