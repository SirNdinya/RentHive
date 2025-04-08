from django.contrib import admin
from .models import Listing

@admin.register(Listing)
class ListingAdmin(admin.ModelAdmin):
    list_display = ('property', 'price', 'status')
    search_fields = ('description',)
    list_filter = ('status',)
