from django.contrib import admin
from django.contrib.auth.admin import UserAdmin
from .models import CustomUser, Tenant, PropertyOwnerProfile, University


# Define an inline admin class for Tenant model
class TenantInline(admin.StackedInline):
    model = Tenant
    can_delete = False
    extra = 1
    fields = ("university",)
    verbose_name = "Tenant Profile"


# Define an inline admin class for PropertyOwnerProfile model
class PropertyOwnerProfileInline(admin.StackedInline):
    model = PropertyOwnerProfile
    can_delete = False
    extra = 1
    fields = ("phone_number",)
    verbose_name = "Owner Profile"


# Customizing the Django UserAdmin for CustomUser model
class CustomUserAdmin(UserAdmin):
    model = CustomUser
    list_display = ('username', 'email', 'role', 'is_staff', 'is_active')  # Removed is_approved
    list_filter = ('role', 'is_staff', 'is_active')  # Removed is_approved

    # Extend default fieldsets to remove the is_approved field
    fieldsets = UserAdmin.fieldsets + (
        ('Custom Fields', {'fields': ('role',)}),  # Removed is_approved
    )

    # Extend default add fieldsets to remove the is_approved field
    add_fieldsets = UserAdmin.add_fieldsets + (
        ('Custom Fields', {'fields': ('role',)}),  # Removed is_approved
    )

    inlines = [TenantInline, PropertyOwnerProfileInline]

    def get_inline_instances(self, request, obj=None):
        inlines = []
        if obj:
            if obj.role == CustomUser.Role.TENANT:
                inlines.append(TenantInline(self.model, self.admin_site))
            elif obj.role == CustomUser.Role.PROPERTY_OWNER:
                inlines.append(PropertyOwnerProfileInline(self.model, self.admin_site))
        return inlines


# Admin configuration for the Tenant model
@admin.register(Tenant)
class TenantAdmin(admin.ModelAdmin):
    list_display = ("user", "university")
    search_fields = ("user__username", "user__email", "university")
    raw_id_fields = ('user',)


# Admin configuration for the PropertyOwnerProfile model
@admin.register(PropertyOwnerProfile)
class PropertyOwnerProfileAdmin(admin.ModelAdmin):
    list_display = ("user", "phone_number")  # Removed is_approved
    search_fields = ("user__username", "user__email", "phone_number")
    raw_id_fields = ('user',)


from django.contrib import admin
from django.contrib.admin.models import LogEntry
from django.shortcuts import render
from django.http import HttpResponseRedirect
from django.urls import path

class LogEntryAdmin(admin.ModelAdmin):
    list_display = ['user', 'content_type', 'object_id', 'action_time', 'action_flag']

    def get_urls(self):
        urls = super().get_urls()
        custom_urls = [
            path('view_actions/', self.view_actions, name='view_actions'),
            path('clear_actions/', self.clear_actions, name='clear_actions'),
        ]
        return custom_urls + urls

    def view_actions(self, request):
        # Get the actions history
        actions = LogEntry.objects.all().order_by('-action_time')
        context = {
            'actions': actions,
            'title': 'Actions History'
        }
        return render(request, 'view_actions.html', context)

    def clear_actions(self, request):
        # Clear all the logs
        LogEntry.objects.all().delete()
        self.message_user(request, "All action logs have been cleared.")
        return HttpResponseRedirect('../')

admin.site.register(CustomUser, CustomUserAdmin)
admin.site.register(University)
admin.site.register(LogEntry, LogEntryAdmin)