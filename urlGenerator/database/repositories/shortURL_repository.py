from .client import Client
from .token_repository import TokenRepo
import datetime
from typing import List
class ShortUrlRepo(object):
    DB = Client.getDb()
    COL = DB["shortUrl"]
    # create a short url. duration in minutes.

    # create a shortUrl record, domain is the domain name of our service
    @staticmethod
    def createShortUrl(originalUrl: str, domain: str, duration: int) -> object:
        token = TokenRepo.getToken()
        shortUrl = f"http://{domain}.com/{token}"
        createdAt = datetime.datetime.utcnow()
        expireAt = createdAt + datetime.timedelta(minutes=duration)
        newRecord = {"shortUrl": shortUrl, "originalUrl": originalUrl, "token": token, "createdAt": createdAt, "expireAt": expireAt}
        res = ShortUrlRepo.COL.insert_one(newRecord)
        if not res: 
            print('fail to insert!')
            return None
        else:
            return newRecord
    @staticmethod
    def getOriginalUrl(shortUrl: str) -> str:
        record = ShortUrlRepo.COL.find_one({"shortUrl": shortUrl})
        if not record:
            return None  # type: ignore
        return record["originalUrl"]
    @staticmethod
    def getShortUrl(id: str) -> str:
        record = ShortUrlRepo.COL.find_one({"_id" :id})
        if not record:
            return None  # type: ignore
        return record["shortUrl"]
    ##################The following functions are for testing purpose#############3

    @staticmethod
    def getAllShortUrl() -> List[object]:
        return ShortUrlRepo.COL.find()
        


