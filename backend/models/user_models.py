from pydantic import BaseModel
from typing import Optional, Union


class User(BaseModel):
    id: int
    username: str
    password: Optional[str] = None

class UserInDB(User):
    hashed_password: str

class Token(BaseModel):
    access_token: str
    token_type: str


class TokenData(BaseModel):
    username: Union[str, None] = None
    id: Union[int, None] = None

fake_users_db = {}