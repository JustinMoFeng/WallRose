from io import BytesIO
from fastapi import APIRouter, File, UploadFile, Depends, HTTPException
from fastapi.responses import StreamingResponse
from bson import ObjectId
from database import get_user_collection
from dependencies.auth_dependencies import get_current_user

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

@router.get("/avatar", response_class=StreamingResponse)
async def get_avatar(user=Depends(get_current_user)):
    """
    根据用户 id 返回用户头像
    """
    if user and user.get("avatar"):
        return StreamingResponse(BytesIO(user["avatar"]), media_type="image/png")
    else:
        raise HTTPException(status_code=404, detail="User avatar not found")
    
@router.put("/password")
async def change_password(old_password: str, new_password: str, collection=Depends(get_user_collection), user=Depends(get_current_user)):
    """
    修改密码
    """
    if user:
        if user["password"] == old_password:
            await collection.update_one({"username":user["username"]},{"$set":{"password":new_password}})
            return {"message":"Change password success"}
        else:
            raise HTTPException(status_code=400, detail="Old password is wrong")
    else:
        raise HTTPException(status_code=404, detail="User not found")
    
    
@router.put("/nickname")
async def change_nickname(nickname: str, collection=Depends(get_user_collection), user=Depends(get_current_user)):
    """
    修改昵称
    """
    if user:
        await collection.update_one({"username":user["username"]},{"$set":{"nickname":nickname}})
        return {"message":"Change nickname success"}
    else:
        raise HTTPException(status_code=404, detail="User not found")
    