from django.http import HttpResponseForbidden
from django.shortcuts import render, redirect
from django.contrib.auth.decorators import login_required
from .models import Property
from .forms import PropertyForm
from django.contrib import messages

# View to display all properties (user can view properties)
@login_required
def property_list(request):
    properties = Property.objects.all()
    return render(request, 'property/property_list.html', {'properties': properties})


@login_required
def property_create(request):
    # Ensure that only PROPERTY_OWNERs can create properties
    if request.user.role != 'PROPERTY_OWNER':
        return HttpResponseForbidden("Only property owners can create properties.")

    if request.method == 'POST':
        form = PropertyForm(request.POST, request.FILES)
        if form.is_valid():
            # Associate the property with the current logged-in user
            property = form.save(commit=False)
            property.owner = request.user
            property.save()
            return redirect('property_list')  # Or replace with your own URL
    else:
        form = PropertyForm()

    return render(request, 'property/property_form.html', {'form': form})


# View to approve or reject a property (only for admin users)
@login_required
def property_approval(request, property_id):
    try:
        property = Property.objects.get(id=property_id)
    except Property.DoesNotExist:
        messages.error(request, "Property not found.")
        return redirect('property_list')

    # Only allow admin to approve/reject
    if not request.user.is_staff:
        messages.error(request, "You do not have permission to approve/reject properties.")
        return redirect('property_list')

    if request.method == 'POST':
        action = request.POST.get('action')
        if action == 'approve':
            property.status = 'approved'
        elif action == 'reject':
            property.status = 'rejected'
        else:
            messages.error(request, "Invalid action.")
            return redirect('property_list')

        property.save()
        messages.success(request, f"Property {action}d successfully!")
        return redirect('property_list')

    return render(request, 'property/property_approval.html', {'property': property})
