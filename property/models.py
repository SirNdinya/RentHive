from django.db import models
from RentHive import settings


class Property(models.Model):
    # Choices for property approval status
    STATUS_CHOICES = (
        ('pending', 'Pending'),  # Property is awaiting approval
        ('approved', 'Approved'),  # Property is approved
        ('rejected', 'Rejected'),  # Property is rejected
    )

    # Field for the name of the property (e.g., apartment name, building name, etc.)
    name = models.CharField(max_length=200)

    # Field for the location of the property (e.g., city, neighborhood)
    location = models.CharField(max_length=300)
    area = models.CharField(max_length=300, default='')

    # A detailed description of the property
    description = models.TextField()

    # Field for uploading the main image of the property
    image = models.ImageField(upload_to='property/images/')

    # Modify the document upload path to store inside the property app folder
    document = models.FileField(upload_to='property/documents/', null=True, blank=True)  # Optional document upload

    # The status of the property (default is 'pending' until it's approved/rejected by the admin)
    status = models.CharField(max_length=10, choices=STATUS_CHOICES, default='pending')

    # Linking property to the owner (uses Django's built-in User model)
    owner = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE)

    # Automatically record the date when the property was added
    date_added = models.DateTimeField(auto_now_add=True)

    # String representation of the Property object
    def __str__(self):
        return self.name
