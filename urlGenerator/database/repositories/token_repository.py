from .client import Client
from secrets import token_urlsafe
import datetime
from typing import List
class TokenRepo(object):
    DB = Client.getDb()
    COL = DB["token"]

    #get a avaliable token
    @staticmethod
    def getToken() -> str:
        record = TokenRepo.COL.find_one({"avaliable": True})
        token = record["token"] if record else None
        if not token: #if there is no avaliable token, create a new token
            token = token_urlsafe(6)
            existToken = TokenRepo.COL.find_one({"token": token})
            while existToken:
                token = token_urlsafe(6)
            newRecord = {"token": token, "createdAt": datetime.datetime.utcnow(), "algoType": "base64", "avaliable": False}
            TokenRepo.COL.insert_one(newRecord)
        else:
            TokenRepo.COL.update_one({"token": token}, {"$set": {"avaliable": False}})
        return token

    ##################The following functions are for testing purpose#############3

    @staticmethod
    def getAllToken() -> List[object]:
        return TokenRepo.COL.find()
        



