from django.contrib.auth.models import AbstractUser
from django.db import models

class CustomUser(AbstractUser):
    """
    Custom user model extending Django's AbstractUser.
    Handles authentication and core user attributes across all roles.
    """
    class Role(models.TextChoices):
        ADMIN = "ADMIN", "Admin"
        PROPERTY_OWNER = "PROPERTY_OWNER", "Property Owner"
        TENANT = "TENANT", "Tenant"

    role = models.CharField(
        max_length=20,
        choices=Role.choices,
        default=Role.TENANT
    )
    is_verified = models.BooleanField(
        default=False
    )
    is_approved = models.BooleanField(
        default=False
    )

    def save(self, *args, **kwargs):
        """Auto-set admin role for superusers"""
        if self.is_superuser:
            self.role = self.Role.ADMIN
            self.is_verified = True
            self.is_approved = True
        elif self.role != self.Role.PROPERTY_OWNER:
            self.is_approved = True
        super().save(*args, **kwargs)

class Tenant(models.Model):
    """
    Tenant profile model containing tenant-specific information.
    Related to CustomUser through OneToOne relationship.
    """
    user = models.OneToOneField(
        CustomUser,
        on_delete=models.CASCADE,

    )
    university = models.CharField(
        max_length=255
    )

    def __str__(self):
        return f"{self.user.username} (Tenant)"

class PropertyOwnerProfile(models.Model):
    """
    Property owner profile containing owner-specific information.
    Note: Property-specific fields moved to properties app.
    """
    user = models.OneToOneField(
        CustomUser,
        on_delete=models.CASCADE,
    )
    phone_number = models.CharField(
        max_length=15
    )
    class Meta:
        verbose_name = "Property Owner "
        verbose_name_plural = "Property Owners"

    def __str__(self):
        return f"{self.user.username} (Property Owner)"

class University(models.Model):
    """
    Reference model for universities in the system.
    """
    name = models.CharField(
        max_length=255,
        unique=True,

    )

    class Meta:
        verbose_name_plural = 'Universities'
        ordering = ['name']

    def __str__(self):
        return self.name