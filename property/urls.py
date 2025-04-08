from django.urls import path
from . import views

urlpatterns = [
    path('', views.property_list, name='property_list'),
    path('create/', views.property_create, name='property_create'),
    path('approval/<int:property_id>/', views.property_approval, name='property_approval'),
]
