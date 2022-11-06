
import pymongo

class Client(object):
    @staticmethod
    def getDb():
        CONNECTION_STRING ="mongodb+srv://admin:admin@cluster0.cwmlq5n.mongodb.net/?retryWrites=true&w=majority"
        client = pymongo.MongoClient(CONNECTION_STRING)
        db = client["SHORTEN_URL_SERVICE"]
        return db
