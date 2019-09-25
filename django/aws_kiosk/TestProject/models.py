from django.db import models

# class Name(models.Model):
#     id   = models.AutoField(primary_key=True)
#     name = models.CharField(max_length=200)

class PreModelUpdateTime(object):

    def __init__(self, time_str):
        self.time = time_str

    def to_dict(self):

        dest = {
            u'pre_update_time':self.time
        }

        return dest


# class Info(object):
    
#     def __init__(self,n):
#         self.number = n

#     def to_dict(self):

#         dest = {
#             u'number': self.number
#         }

#         return dest
