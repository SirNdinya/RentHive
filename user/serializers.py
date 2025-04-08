# serializers.py (user app)

# Import Django REST framework's serializer module
from rest_framework import serializers

# Import the custom user model and profile models
from .models import CustomUser, Tenant, PropertyOwnerProfile as PropertyOwner

# Serializer for the CustomUser model
class CustomUserSerializer(serializers.ModelSerializer):
    class Meta:
        model = CustomUser  # Specify the model to be serialized
        fields = ['id', 'username', 'email', 'role']  # Fields to include in the serialized output

# Serializer for the Tenant model
class TenantSerializer(serializers.ModelSerializer):
    user = CustomUserSerializer()  # Nest the CustomUserSerializer to include user details

    class Meta:
        model = Tenant  # Specify the model to be serialized
        fields = '__all__'  # Include all fields from the Tenant model

# Serializer for the PropertyOwner model
class PropertyOwnerSerializer(serializers.ModelSerializer):
    user = CustomUserSerializer()  # Nest the CustomUserSerializer to include user details

    class Meta:
        model = PropertyOwner  # Specify the model to be serialized
        fields = '__all__'  # Include all fields from the PropertyOwner model
