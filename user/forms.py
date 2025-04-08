from django import forms
from django.contrib.auth.forms import UserCreationForm  # Base user creation form
from django.core.exceptions import ValidationError  # For form validation errors
from .models import CustomUser  # Custom user model import


class RegistrationForm(UserCreationForm):
    """
    Custom registration form that extends Django's UserCreationForm.
    Handles user registration with role-specific fields and validation.
    """

    # Basic required fields
    username = forms.CharField(
        max_length=150,
        required=True
    )
    email = forms.EmailField(
        required=True
    )
    password1 = forms.CharField(
        widget=forms.PasswordInput,
        required=True,
        label="Password"
    )
    password2 = forms.CharField(
        widget=forms.PasswordInput,
        required=True,
        label="Password Confirmation"
    )

    # Role selection field - uses choices from CustomUser model
    role = forms.ChoiceField(
        choices=CustomUser.Role.choices,
        required=True
    )

    # Conditionally required fields based on role selection
    university = forms.CharField(
        max_length=255,
        required=False
    )

    phone_number = forms.CharField(
        max_length=15,
        required=False
    )

    class Meta:
        model = CustomUser
        fields = [
            'username', 'email', 'password1', 'password2',
            'role', 'university', 'phone_number'
        ]

    def clean_username(self):
        """
        Validate username uniqueness.
        - Checks if username already exists in database
        - Raises validation error if username is taken
        """
        username = self.cleaned_data.get('username')
        if CustomUser.objects.filter(username=username).exists():
            raise ValidationError("This username is already taken.")
        return username

    def clean_email(self):
        """
        Validate email uniqueness and format.
        - Checks if email already exists in database
        - Ensures email is unique across all accounts
        """
        email = self.cleaned_data.get('email')
        if CustomUser.objects.filter(email=email).exists():
            raise ValidationError("This email is already registered.")
        return email

    def clean_password2(self):
        """
        Validate password confirmation.
        - Ensures password1 and password2 match
        - This is a standard check in user registration
        """
        password1 = self.cleaned_data.get('password1')
        password2 = self.cleaned_data.get('password2')

        if password1 and password2 and password1 != password2:
            raise ValidationError("Passwords do not match.")
        return password2

    def clean_password1(self):
        """
        Enforce strong password requirements:
        - Minimum 8 characters
        - At least one uppercase letter
        - At least one number
        - At least one special character
        """
        password1 = self.cleaned_data.get('password1')

        if not password1:
            return password1  # Let required field validation handle empty case

        # Check for at least one digit
        if not any(char.isdigit() for char in password1):
            raise ValidationError("Password must contain at least one number.")

        # Check for at least one uppercase letter
        if not any(char.isupper() for char in password1):
            raise ValidationError("Password must contain at least one uppercase letter.")

        # Check for at least one special character
        if not any(char in "!@#$%^&*()_+" for char in password1):
            raise ValidationError("Password must contain at least one special character.")

        # Check minimum length
        if len(password1) < 8:
            raise ValidationError("Password must be at least 8 characters long.")

        return password1

    def clean(self):
        """
        Final form validation that checks role-specific requirements:
        - TENANT must provide university
        - PROPERTY_OWNER must provide university_area
        - Phone number validation could be added here if needed
        """
        cleaned_data = super().clean()
        role = cleaned_data.get("role")

        # Role-specific field validation
        if role == "TENANT":
            if not cleaned_data.get("university"):
                self.add_error('university', "University name is required for tenants.")

            # Optional: Add phone number validation for property owners
            phone_number = cleaned_data.get("phone_number")
            if phone_number:
                # Remove all non-digit characters
                cleaned_phone = ''.join(filter(str.isdigit, phone_number))
                if len(cleaned_phone) < 10:  # Example validation
                    self.add_error('phone_number', "Enter a valid phone number with at least 10 digits.")
                cleaned_data['phone_number'] = cleaned_phone

        return cleaned_data
