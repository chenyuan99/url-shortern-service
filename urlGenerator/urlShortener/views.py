from email import message
from django.shortcuts import render
from rest_framework.decorators import api_view, renderer_classes
from rest_framework.response import Response
from urlShortener.models import url
import random

# Create your views here.
def token_gen(url):
    return (str) (random.randint(0, 10))

@api_view(('POST',))
def shorten(request):
    long_url = request.POST["long_url"]
    source, created = url.objects.get_or_create(origin_url=long_url)
    token = ""
    if not created:
        token = source.token
    else:
        source.user_name = "tmp"
        source.origin_url = long_url
        token = token_gen(long_url)
        source.token = token
        source.save()
    return Response(data={"shorten_url": "https://" + "domain/" + token})

@api_view(('POST',))
def resolve(request):
    try:
        short_url = request.POST["short_url"]
        token = short_url.split("/")[-1]
        long_url = url.objects.get(token=token).origin_url
        return Response(data={"resolved_url": long_url})
    except:
        return Response(data={"message":"URL not found!"}, status=404)