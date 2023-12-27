from fastapi import APIRouter, HTTPException, Depends, status
from fastapi.security import OAuth2PasswordRequestForm
from models.user_models import User, UserInDB, fake_users_db, Token
from dependencies.auth_dependencies import verify_password, hash_password, create_access_token, get_current_user, authenticate_user
from models.result_models import Result
from datetime import timedelta
import os

router = APIRouter()

@router.post("/register")
async def register(user: User):
    # print(user)
    if user.username in fake_users_db:
        raise HTTPException(status_code=400, detail="Username already registered")
    hashed_password = hash_password(user.password)
    # 此处应该是将用户信息存入数据库
    fake_users_db[user.username] = UserInDB(id=user.id ,username=user.username, hashed_password=hashed_password)
    token_dict = {"username":user.username,"id":user.id}
    expire_minutes = int(os.getenv("ACCESS_TOKEN_EXPIRE_MINUTES"))
    my_token = create_access_token(token_dict,timedelta(minutes=expire_minutes))
    return {"message":"Register Success"}


@router.post("/login", response_model=Token)
async def login(form_data: OAuth2PasswordRequestForm = Depends()):
    user = authenticate_user(fake_users_db, form_data.username, form_data.password)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect username or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    token_dict = {"username":user.username,"id":user.id}
    expire_minutes = int(os.getenv("ACCESS_TOKEN_EXPIRE_MINUTES"))
    my_token = create_access_token(token_dict,timedelta(minutes=expire_minutes))
    return {"access_token": my_token, "token_type": "bearer"}


@router.get("/users/me", response_model=User)
async def read_users_me(current_user: UserInDB = Depends(get_current_user)):
    return current_user