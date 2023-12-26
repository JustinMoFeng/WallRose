from fastapi import APIRouter, HTTPException, Depends
from fastapi.security import OAuth2PasswordRequestForm
from models.user_models import User, UserInDB, fake_users_db
from dependencies.auth_dependencies import verify_password, hash_password, create_access_token
from models.result_models import Result
from datetime import timedelta
import os
from dotenv import load_dotenv

router = APIRouter()

@router.post("/register")
async def register(user: User):
    # print(user)
    load_dotenv()
    if user.username in fake_users_db:
        return Result(code=400,message="Username already registered")
    hashed_password = hash_password(user.password)
    # 此处应该是将用户信息存入数据库
    fake_users_db[user.username] = UserInDB(id=user.id ,username=user.username, hashed_password=hashed_password)
    token_dict = {"username":user.username,"id":user.id}
    expire_minutes = int(os.getenv("ACCESS_TOKEN_EXPIRE_MINUTES"))
    my_token = create_access_token(token_dict,timedelta(minutes=expire_minutes))
    data = {"token":my_token,"username":user.username}
    return Result(code=200,message="Register Successfull",data=data)


@router.post("/login")
async def login(form_data: OAuth2PasswordRequestForm = Depends()):
    load_dotenv()
    user_dict = fake_users_db.get(form_data.username)
    if not user_dict:
        return Result(code=400,message="Incorrect username or password")
    if isinstance(user_dict, UserInDB):
        user = user_dict
    else:
        user = UserInDB(**user_dict)
    if not verify_password(form_data.password, user.hashed_password):
        return Result(code=400,message="Incorrect username or password")
    token_dict = {"username":user.username,"id":user.id}
    print(token_dict)
    expire_minutes = int(os.getenv("ACCESS_TOKEN_EXPIRE_MINUTES"))
    my_token = create_access_token(token_dict,timedelta(minutes=expire_minutes))
    data = {"token":my_token,"username":user.username}
    return Result(code=200,message="Login Successfull",data=data)