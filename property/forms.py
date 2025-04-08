from django import forms
from .models import Property


class PropertyForm(forms.ModelForm):
    """
    A form for creating and editing properties.
    This includes fields like the name, location, description, image, and document.
    """

    class Meta:
        model = Property
        fields = ['name', 'location','area', 'description', 'image', 'document']

    # Custom validation to ensure all fields are properly filled out
    def clean(self):
        cleaned_data = super().clean()
        image = cleaned_data.get('image')
        document = cleaned_data.get('document')

        # Ensure the property has an image uploaded
        if not image:
            raise forms.ValidationError("Image is required for the property.")

        # Document is optional, but if uploaded, check if it's a valid file type
        if document and not document.name.endswith(('.pdf', )):
            raise forms.ValidationError("Document must be in PDF or Word format.")

        return cleaned_data
