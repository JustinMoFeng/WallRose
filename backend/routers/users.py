from fastapi import APIRouter, File, UploadFile, Depends
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
    return {"message":"Upload Success"}
    