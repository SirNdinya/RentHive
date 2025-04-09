from django import forms
from .models import Property

class PropertyForm(forms.ModelForm):
    """
    A form for creating and editing properties.
    This includes fields like the name, location, description, image, and document.
    """

    class Meta:
        model = Property
        fields = ['name', 'location', 'area', 'description', 'image', 'document']

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)

        # Adding help texts for each field
        self.fields['name'].help_text = 'Property/Rental name'
        self.fields['location'].help_text = 'The university location'
        self.fields['area'].help_text = 'Specific area'
        self.fields['description'].help_text = " e.g An Apartment owned by Jane Smith. Amenities: WiFi, air conditioning, gym, and private parking. (Ensure original names as per Property Registration Certificate, for approval)"
        self.fields['document'].help_text = 'Upload your Property Registration Certificate for approval'

        # Set the document field as required
        self.fields['document'].required = True

    # Custom validation to ensure all fields are properly filled out
    def clean(self):
        cleaned_data = super().clean()
        image = cleaned_data.get('image')
        document = cleaned_data.get('document')

        # Ensure the property has an image uploaded
        if not image:
            raise forms.ValidationError("Image is required for the property.")

        # Ensure the document is provided
        if not document:
            raise forms.ValidationError("Document is required for the property.")

        # If document is uploaded, check if it's a valid file type (PDF or Word)
        if document and not document.name.endswith(('.pdf', '.docx')):
            raise forms.ValidationError("Document must be in PDF or Word format.")

        return cleaned_data
