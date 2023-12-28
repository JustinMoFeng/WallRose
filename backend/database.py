from motor.motor_asyncio import AsyncIOMotorClient
from motor.motor_asyncio import AsyncIOMotorCollection

from config import config

def connect_mongo():
    # MongoDB
    client = AsyncIOMotorClient(config["mongo"]["url"])
    database = client[config["mongo"]["database"]]
    return database

mongodb = connect_mongo()

def get_collection(collection_name: str):
    return mongodb[collection_name]

def get_user_collection():
    return get_collection("users")

def get_chat_collection():
    return get_collection("chats")