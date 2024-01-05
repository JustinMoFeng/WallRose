from pydantic import BaseModel, Field

from . import PyObjectId

class User(BaseModel):
    username: str
    nickname: str
    password: str | None = None
    avatarUrl: str | None = None

class UserInDB(User):
    user_id: PyObjectId | None = Field(alias="_id", default=None)
    hashed_password: str
    avatar: bytes | None = None
    image_id: str | None = None

class Token(BaseModel):
    access_token: str
    token_type: str

class TokenData(BaseModel):
    username: str | None = None
    user_id: str | None = None