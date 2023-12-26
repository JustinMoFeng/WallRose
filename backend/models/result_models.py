from pydantic import BaseModel
from typing import Optional,Any

class Result(BaseModel):
    code: int
    message: str
    data: Optional[Any] = None