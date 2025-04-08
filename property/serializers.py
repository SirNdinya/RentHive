from rest_framework import serializers
from .models import Property


class PropertySerializer(serializers.ModelSerializer):
    """
    Serializer for the Property model.
    This is used for converting Property objects to JSON and vice versa.
    """

    class Meta:
        model = Property
        fields = ['id', 'name', 'location', 'description', 'image', 'document', 'status', 'owner', 'date_added']

    # Custom validation to check for required fields
    def validate(self, data):
        # Ensure that the image is uploaded
        if 'image' not in data:
            raise serializers.ValidationError("Image is required.")

        return data
