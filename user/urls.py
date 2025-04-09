from django.conf.urls.static import static
from django.contrib.auth.views import LogoutView
from django.urls import path

from RentHive import settings
from . import views
from .views import activate_view, university_suggestions, custom_logout_view, CustomPasswordResetView
from django.contrib.auth import views as auth_views

urlpatterns = [
    path('register/', views.register_view, name='register'),
    path('login/', views.login_view, name='login'),

    path('university-suggestions/', university_suggestions, name='university_suggestions'),

    path('activate/<uidb64>/<token>/', activate_view, name='activate'),
    path('logout/', custom_logout_view, name='logout'),
    path('password_reset/', CustomPasswordResetView.as_view(), name='password_reset'),    path('password_reset/done/',auth_views.PasswordResetDoneView.as_view(template_name='user/password_reset_done.html'),name='password_reset_done'),
    path('reset/<uidb64>/<token>/',auth_views.PasswordResetConfirmView.as_view(template_name='user/password_reset_confirm.html'),name='password_reset_confirm'),
    path('reset/done/',auth_views.PasswordResetCompleteView.as_view(template_name='user/password_reset_complete.html'),name='password_reset_complete'),
              ]+ static(settings.STATIC_URL, document_root=settings.STATIC_ROOT)
