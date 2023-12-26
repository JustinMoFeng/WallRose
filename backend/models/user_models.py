from pydantic import BaseModel
from typing import Optional


class User(BaseModel):
    id: int
    username: str
    password: str

class UserInDB(User):
    hashed_password: str
    password: Optional[str] = None

fake_users_db = {}