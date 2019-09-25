"""TestProject URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/2.1/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path
# from .views import hello, index, saveToDb, testsisi,lambda_handler, itemCF,xgb_recom
from . import views

urlpatterns = [
    path('admin/', admin.site.urls),

    #path('home_recom/<humidity>/<temp>/<speed>/<emo1>/<emo2>', views.lambda_handler), #humidity,temp,speed,emotion
    
    # xgb 분류 알고리즘으로 아이템 유사도 행렬 결과 반환 -- 타추천
    # /xgb_recom/2019-09-09%2021:05:58/80.0/300.0/1.00/happiness/neutral
    path('xgb_recom/<today>/<humidity>/<temp>/<speed>/<emo1>/<emo2>', views.xgb_recom),

    # path('hello/<name>', hello),
    # path('savetodb/<name>', saveToDb),
    # 선호도 기반 유사도 행렬 결과: 타추천, 상세
    # /item_cf/911283bd-2e4f-439b-89c4-fc8667782b51
    path('item_cf/<user_uuid>', views.itemCF), #userid말고 uuid를 주겠지 함수에서 변경해야 한다
    
    # 날씨,감정 기반 유사도 행렬 결과: 상세
    path('weather_emotion_recom/', views.weather_emotion_recom),

    # path('testsisi/', testsisi),
    path('', views.index) # 이게 기본url? 어쨌든 이 아래에 path()있으면 nginx에서 인식안된다 시발
    
]
