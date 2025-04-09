from django.utils.encoding import force_str  # For safely converting bytes to strings
from django.utils.http import urlsafe_base64_decode  # For decoding URL-safe base64 strings
from .models import CustomUser  # Import custom user model
from django.contrib.auth import get_user_model  # Retrieve the active user model
from django.contrib.auth import authenticate, login  # Functions for user authentication and login
from django.contrib.auth.forms import AuthenticationForm  # Django's built-in authentication form
from django.shortcuts import render, redirect  # Functions to render templates and handle redirections
from django.contrib import messages  # For displaying user messages (success, error, etc.)
from django.db import transaction  # For database transaction management
import re  # Regular expressions for phone number validation and cleaning
from django.http import JsonResponse  # For sending JSON responses (e.g., university suggestions)
from .models import University  # Import University model
from .forms import RegistrationForm  # Import custom registration form
from .models import Tenant, PropertyOwnerProfile as PropertyOwner  # Additional user profile models
from django.core.mail import EmailMultiAlternatives
from django.template.loader import render_to_string  # For rendering email templates
from django.utils.html import strip_tags  # Convert HTML content to plain text
from django.utils.http import urlsafe_base64_encode  # Encode user ID securely
from django.utils.encoding import force_bytes  # Convert user ID to bytes
from django.contrib.auth.tokens import default_token_generator  # Generate account activation token
from django.urls import reverse  # For generating activation URL
from django.conf import settings  # Access project settings
from django.contrib.auth.views import PasswordResetView
from django.urls import reverse_lazy


# Get the currently active user model (default or custom)
User = get_user_model()

def login_view(request):
    """
    Handle user login requests.
    - Redirects authenticated users away from login page
    - Validates credentials and checks account activation status
    - Shows appropriate error messages for various failure cases
    """
    if request.user.is_authenticated:
        return redirect('home')  # Prevent logged-in users from accessing login page

    if request.method == 'POST':
        username = request.POST.get('username')
        password = request.POST.get('password')

        try:
            # First, check if the user exists in the database
            user = User.objects.get(username=username)
        except User.DoesNotExist:
            messages.error(request, "Username not found.")
            return redirect('login')

        # Check if the account is inactive (i.e., email not verified)
        if not user.is_active:
            messages.error(request, "Please verify your email before logging in.")
            return redirect('login')

        # Authenticate the user with the provided credentials
        user = authenticate(request, username=username, password=password)
        if user is None:
            messages.error(request, "Invalid password.")
        else:
            # Successful login
            login(request, user)

            # Role-based redirects
            if user.role == CustomUser.Role.PROPERTY_OWNER:
                return redirect('property_list')  # Redirect to property owner dashboard
            elif user.role == CustomUser.Role.TENANT:
                return redirect('tenant_listings')  # Still redirect to home but with message
            else:  # Admin or other roles
                return redirect('register')  # Default redirect


    # For GET requests or failed POST attempts, display the login form
    form = AuthenticationForm()
    return render(request, 'user/login.html', {'form': form})


def send_verification_email(user, request):
    """
    Send an email verification link to a newly registered user.
    """
    token = default_token_generator.make_token(user)  # Generate unique activation token
    uidb64 = urlsafe_base64_encode(force_bytes(user.pk))  # Encode user ID
    activation_url = request.build_absolute_uri(
        reverse('activate', kwargs={'uidb64': uidb64, 'token': token})
    )

    subject = "Activate Your Account"
    html_content = render_to_string("user/email_verification.html", {"user": user, "activation_url": activation_url})
    text_content = strip_tags(html_content)  # Plain text fallback for email clients that don't support HTML

    email = EmailMultiAlternatives(subject, text_content, settings.DEFAULT_FROM_EMAIL, [user.email])
    email.attach_alternative(html_content, "text/html")  # Attach HTML content
    email.send()  # Send the email

def register_view(request):
    """
    Handle new user registration.
    - Processes registration form data
    - Creates appropriate user profile based on role (tenant/property owner)
    - Sends verification email
    - Handles errors with specific messages
    """
    if request.method == "POST":
        form = RegistrationForm(request.POST)
        if form.is_valid():
            try:
                with transaction.atomic():  # Ensures all operations succeed or fail together
                    user = form.save(commit=False)
                    user.role = form.cleaned_data["role"]  # Assign user role
                    user.is_active = False  # Require email verification before activation
                    user.save()

                    # Create appropriate profile based on user role
                    if user.role == "TENANT":
                        Tenant.objects.create(user=user, university=form.cleaned_data["university"])
                    elif user.role == "PROPERTY_OWNER":
                        cleaned_phone = re.sub(r"\D", "", form.cleaned_data["phone_number"])  # Remove non-numeric characters
                        PropertyOwner.objects.create(user=user, phone_number=cleaned_phone)

                    # Send verification email to user
                    send_verification_email(user, request)
                    messages.success(request, "Check your email to activate your account.")
                    return render(request, "user/register.html", {"form": RegistrationForm(), "redirect_to_login": True})
            except Exception:
                messages.error(request, "Registration failed. Please try again.")
        else:
            for field, errors in form.errors.items():
                for error in errors:
                    messages.error(request, f"{error}")
    else:
        form = RegistrationForm()

    return render(request, "user/register.html", {"form": form})

def activate_view(request, uidb64, token):
    """
    Handle account activation via email verification link.
    """
    try:
        uid = force_str(urlsafe_base64_decode(uidb64))  # Decode user ID from URL
        user = CustomUser.objects.get(pk=uid)  # Retrieve user from database
    except (TypeError, ValueError, OverflowError, CustomUser.DoesNotExist):
        user = None

    if user is not None and default_token_generator.check_token(user, token):
        user.is_active = True  # Activate the user's account
        user.save()
        messages.success(request, "Your account has been activated. You can now log in.")
        return redirect('login')
    else:
        messages.error(request, "Invalid activation link.")
        return redirect('login')

def university_suggestions(request):
    """
    Provide university name suggestions based on user input.
    """
    query = request.GET.get('q', '')  # Get the search query from the request
    universities = University.objects.filter(name__icontains=query)  # Search for matching universities
    university_names = list(universities.values_list('name', flat=True))  # Extract university names
    return JsonResponse(university_names, safe=False)  # Return JSON response with university names


from django.contrib.auth import logout
from django.shortcuts import redirect

def custom_logout_view(request):
    logout(request)
    return redirect('login')  # redirect to login page after logout


from django.contrib.auth.views import PasswordResetView
from django.urls import reverse_lazy
from django.contrib.auth.forms import PasswordResetForm
from django.template.loader import render_to_string
from django.core.mail import send_mail
from django.utils.translation import gettext_lazy as _

class CustomPasswordResetView(PasswordResetView):
    template_name = 'user/password_reset.html'  # Specify the template for the password reset form
    html_email_template_name = 'user/password_reset_email.html'  # Specify the custom email template
    success_url = reverse_lazy('password_reset_done')  # Redirect to 'password_reset_done' after successful reset
    subject_template_name = 'user/password_reset_subject'  # Add custom subject template here

    def form_valid(self, form):
        """
        Override form_valid to add custom behavior (check if email exists).
        """
        # The form is valid, but before proceeding, let's check if the email exists in the database
        email = form.cleaned_data["email"]
        try:
            # Check if the email exists in the database
            user = User.objects.get(email=email)
        except User.DoesNotExist:
            # If the email does not exist, we can handle the error here
            form.add_error("email", "This email address is not registered")
            return self.form_invalid(form)

        # If email exists, continue the default behavior (sending the reset email)
        return super().form_valid(form)
