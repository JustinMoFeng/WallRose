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
    return {"message":"Upload Success"}

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