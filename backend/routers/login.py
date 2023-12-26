from fastapi import APIRouter, HTTPException, Depends
from fastapi.security import OAuth2PasswordRequestForm
from models.user_models import User, UserInDB, fake_users_db
from dependencies.auth_dependencies import verify_password, hash_password, create_access_token
from models.result_models import Result
import os

router = APIRouter()

@router.post("/register")
async def register(user: User):
    print(user)
    if user.username in fake_users_db:
        return Result(code=400,message="Username already registered")
    hashed_password = hash_password(user.password)
    # 此处应该是将用户信息存入数据库
    fake_users_db[user.username] = UserInDB(id=user.id ,username=user.username, hashed_password=hashed_password)
    token_dict = {"username":user.username,"id":user.id}
    my_token = create_access_token(token_dict,os.getenv("ACCESS_TOKEN_EXPIRE_MINUTES"))
    data = {"token":my_token,"username":user.username}
    return Result(code=200,message="Register Successfull",data=data)


@router.post("/login")
async def login(form_data: OAuth2PasswordRequestForm = Depends()):
    user_dict = fake_users_db.get(form_data.username)
    if not user_dict:
        raise HTTPException(status_code=400, detail="Incorrect username or password")
    
    user = UserInDB(**user_dict)
    if not verify_password(form_data.password, user.hashed_password):
        raise HTTPException(status_code=400, detail="Incorrect username or password")
    # 这里应该生成一个token，但为了简化，我们只返回一个消息
    return {"message": "Login successful"}