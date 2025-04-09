from django import forms
from .models import Listing, Property
from django.core.exceptions import ValidationError

class ListingForm(forms.ModelForm):
    class Meta:
        model = Listing
        fields = ['property', 'price', 'description', 'status', 'image']

    def __init__(self, *args, **kwargs):
        # Capture the user instance
        self.user = kwargs.pop('user', None)
        super().__init__(*args, **kwargs)

        # Filter the properties to only show those owned by the logged-in user
        if self.user:
            self.fields['property'].queryset = Property.objects.filter(owner=self.user)


        self.fields['price'].widget.attrs['placeholder'] = 'Ksh.'
        self.fields['description'].widget.attrs['placeholder'] = 'Add brief description of the vacancy'


    def clean_property(self):
        # Ensure the property belongs to the logged-in user
        prop = self.cleaned_data.get('property')
        if prop and self.user and prop.owner != self.user:
            raise ValidationError("You can only create listings for properties you own.")
        return prop
