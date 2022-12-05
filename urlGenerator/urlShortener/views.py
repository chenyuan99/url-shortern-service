from email import message
from django.shortcuts import render
from rest_framework.decorators import api_view, renderer_classes
from rest_framework.response import Response
from urlShortener.models import url
import random
import time
import string
from database import ShortUrlRepo, TokenRepo

DOMAIN_NAME = "urlService"
EXPIRED_DURATION = 2

def token_gen(url):
    map = string.digits + string.ascii_letters
    s_url = ""
    # Convert Base-62
    id = int(time.time()) * 1000
    while id > 0:
        p = id % 62
        s_url += map[p]
        id = id // 62
    return s_url

@api_view(('POST',))
def shorten(request):
    long_url = request.POST["long_url"]
    result_obj = ShortUrlRepo.createShortUrl(long_url, DOMAIN_NAME, EXPIRED_DURATION)
    id = result_obj.inserted_id
    short_URL = ShortUrlRepo.getShortUrl(id)
    if short_URL == None:
        return Response(data={"message":"URL create error!"}, status=404)
    return Response(data={"shorten_url": short_URL})
    

@api_view(('POST',))
def resolve(request):
    original_URL = ShortUrlRepo.getOriginalUrl(request.POST["short_url"])
    if original_URL == None:
        return Response(data={"message":"URL not found or not exists!"}, status=404)
    else:
        return Response(data={"resolved_url": original_URL})