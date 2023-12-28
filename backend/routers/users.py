from io import BytesIO
from fastapi import APIRouter, File, UploadFile, Depends, HTTPException, status
from fastapi.responses import StreamingResponse
from bson import ObjectId
from database import get_user_collection
from dependencies.auth_dependencies import hash_password, authenticate_user, get_current_user
from pydantic import BaseModel

router = APIRouter()

@router.post("/upload_image")
async def upload_image(file: UploadFile = File(...), collection=Depends(get_user_collection), user=Depends(get_current_user)):
    """
    上传图片并保存，返回一个唯一的 token。
    """
    image_data = await file.read()
    # 更新用户头像
    res = await collection.update_one({"username":user.username},{"$set":{"avatar":image_data}})
    return {"message":"上传成功"}

@router.get("/avatar/{user_id}", response_class=StreamingResponse)
async def get_avatar(user_id: str, collection=Depends(get_user_collection)):
    """
    根据用户 id 返回用户头像
    """
    user = await collection.find_one({"_id":ObjectId(user_id)})
    if user and user.get("avatar"):
        return StreamingResponse(BytesIO(user["avatar"]), media_type="image/png")
    else:
        raise HTTPException(status_code=404, detail="User avatar not found")


class Password(BaseModel):
    old_password: str
    new_password: str
    
@router.put("/users/password")
async def change_password(password: Password, collection=Depends(get_user_collection), user=Depends(get_current_user)):
    """
    修改密码
    """
    user = await authenticate_user(user.username, password.old_password)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="原密码错误",
            headers={"WWW-Authenticate": "Bearer"},
        )
    try:
        hashed_password = hash_password(password.new_password)
        await collection.update_one({"username":user.username},{"$set":{"hashed_password":hashed_password}})
        return {"message":"Change password success"}
    except:
        raise HTTPException(status_code=500, detail="Change password failed")
     
@router.put("/users/nickname")
async def change_nickname(nickname: str, collection=Depends(get_user_collection), user=Depends(get_current_user)):
    """
    修改昵称
    """
    try:
        await collection.update_one({"username":user.username},{"$set":{"nickname":nickname}})
        return {"message":"Change nickname success"}
    except:
        raise HTTPException(status_code=500, detail="Change nickname failed")
    