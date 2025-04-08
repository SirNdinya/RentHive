from django.contrib import admin
from django.utils.html import format_html
from .models import Property


@admin.register(Property)
class PropertyAdmin(admin.ModelAdmin):
    list_display = ('name', 'location', 'status_display', 'date_added', 'owner', 'view_document_link')
    list_filter = ('status', 'owner')
    search_fields = ('name', 'location', 'description')
    ordering = ('-date_added',)

    readonly_fields = ('owner',)

    actions = ['approve_property', 'reject_property']

    def status_display(self, obj):
        if obj.status == 'approved':
            return format_html('<span style="color: green;">✓ </span>')
        elif obj.status == 'rejected':
            return format_html('<span style="color: red;">✗ </span>')
        return obj.get_status_display()
    status_display.short_description = 'Status'
    status_display.admin_order_field = 'status'

    def view_document_link(self, obj):
        if obj.document:
            return format_html('<a href="{}" target="_blank">View Document</a>', obj.document.url)
        return 'No document available'
    view_document_link.short_description = 'Document Link'

    def approve_property(self, request, queryset):
        updated_count = queryset.update(status='approved')
        self.message_user(
            request,
            f'{updated_count} property(ies) were successfully marked as approved.',
            level='success'
        )

    def reject_property(self, request, queryset):
        updated_count = queryset.update(status='rejected')
        self.message_user(
            request,
            f'{updated_count} property(ies) were successfully marked as rejected.',
            level='error'
        )
    approve_property.short_description = "Approve selected properties"
    reject_property.short_description = "Reject selected properties"