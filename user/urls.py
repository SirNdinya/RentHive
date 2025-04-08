from django.conf.urls.static import static
from django.contrib.auth.views import LogoutView
from django.urls import path

from RentHive import settings
from . import views
from .views import activate_view, university_suggestions, custom_logout_view

urlpatterns = [
    path('register/', views.register_view, name='register'),
    path('login/', views.login_view, name='login'),

    path('university-suggestions/', university_suggestions, name='university_suggestions'),

    path('activate/<uidb64>/<token>/', activate_view, name='activate'),
    path('logout/', custom_logout_view, name='logout'),


              ]+ static(settings.STATIC_URL, document_root=settings.STATIC_ROOT)
