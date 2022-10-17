from distutils.text_file import TextFile
from operator import mod
from django.db import models

# Create your models here.
class url(models.Model):
    user_name = models.TextField()
    origin_url = models.TextField()
    token = models.TextField()
    

    class Meta:
        db_table = "url"