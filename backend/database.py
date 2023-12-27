from motor.motor_asyncio import AsyncIOMotorClient

from config import config

def connect_mongo():
    # MongoDB
    client = AsyncIOMotorClient(config["mongo"]["url"])
    database = client[config["mongo"]["database"]]
    return database

mongodb = connect_mongo()

def get_collection(db, collection_name: str):
    return db[collection_name]

def get_user_collection():
    return get_collection(mongodb, "users")