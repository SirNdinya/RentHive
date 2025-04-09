from django.db import models
from property.models import Property  # Assuming your app is named `property`
import uuid

class Listing(models.Model):
    STATUS_CHOICES = [
        ('Available', 'Available'),
        ('Rented', 'Rented'),
    ]

    listing_id = models.UUIDField(default=uuid.uuid4, editable=False, unique=True)
    property = models.ForeignKey(Property, on_delete=models.CASCADE, related_name='listings')
    price = models.DecimalField(max_digits=10, decimal_places=2)
    description = models.TextField()
    status = models.CharField(max_length=20, choices=STATUS_CHOICES, default='Available')
    image = models.ImageField(upload_to='listing_images/', blank=True)

    def __str__(self):
        return f"{self.property.name} - ${self.price}"
