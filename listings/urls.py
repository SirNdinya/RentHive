from django.urls import path
from . import views

urlpatterns = [
    path('', views.listing_list, name='listing_list'),
    path('add/', views.listing_create, name='listing_add'),
    path('update/<int:pk>/', views.listing_update, name='listing_update'),
    path('delete/<int:pk>/', views.listing_delete, name='listing_delete'),
    path('browse/', views.tenant_listing_view, name='tenant_listings'),
    path('properties/<int:property_id>/listings/', views.property_listings, name='property_listings'),


]
