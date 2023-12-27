from fastapi import APIRouter, HTTPException, Depends, status
from fastapi.security import OAuth2PasswordRequestForm
from models.user_models import User, UserInDB, Token
from dependencies.auth_dependencies import hash_password, create_access_token, get_current_user, authenticate_user
from datetime import timedelta
import os
from database import get_user_collection
from dotenv import load_dotenv

load_dotenv()

router = APIRouter()

@router.post("/register")
async def register(user: User, collection=Depends(get_user_collection)):
    # 如果用户名已经存在，返回错误
    if await collection.find_one({"username":user.username}):
        raise HTTPException(status_code=400, detail="Username already registered")
    try:
        hashed_password = hash_password(user.password)
        expire_minutes = int(os.getenv("ACCESS_TOKEN_EXPIRE_MINUTES"))
    except Exception as e:
        raise HTTPException(status_code=500, detail="Internal Server Error")
    # 将用户信息存入mongodb, user.id是mongodb自动生成的_id
    res = await collection.insert_one({"username":user.username,"hashed_password":hashed_password,"nickname":user.nickname})
    token_dict = {"username":user.username,"user_id":str(res.inserted_id)}
    create_access_token(token_dict,timedelta(minutes=expire_minutes))
    return {"message":"Register Success"}


@router.post("/login", response_model=Token)
async def login(form_data: OAuth2PasswordRequestForm = Depends()):
    user = await authenticate_user(form_data.username, form_data.password)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect username or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    token_dict = {"username":user.username,"user_id":str(user.user_id)}
    expire_minutes = int(os.getenv("ACCESS_TOKEN_EXPIRE_MINUTES"))
    my_token = create_access_token(token_dict,timedelta(minutes=expire_minutes))
    return {"access_token": my_token, "token_type": "bearer"}


@router.get("/users/me", response_model=User)
async def read_users_me(current_user: UserInDB = Depends(get_current_user)):
    if current_user.avatar:
        current_user.avatarUrl = f"/avatar/{current_user.user_id}"
    return current_user