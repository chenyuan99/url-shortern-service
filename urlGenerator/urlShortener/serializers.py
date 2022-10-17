from rest_framework import serializers
from urlShortener import url

class UrlSerializer(serializers.ModelSerializer):
    class Meta:
        model = url
        fields = "__all__"