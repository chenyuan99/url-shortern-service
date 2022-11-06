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
        return res
    @staticmethod
    def getOriginalUrl(shortUrl: str) -> str:
        record = ShortUrlRepo.COL.find_one({"shortUrl": shortUrl})
        if not record:
            return None  # type: ignore
        return record["originalUrl"]

    ##################The following functions are for testing purpose#############3

    @staticmethod
    def getAllShortUrl() -> List[object]:
        return ShortUrlRepo.COL.find()
        


