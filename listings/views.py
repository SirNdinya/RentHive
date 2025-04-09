# views.py
from django.shortcuts import render, redirect
from django.contrib.auth.decorators import login_required
from django.http import HttpResponseForbidden
from django.db.models import Q
from .models import Listing
from .forms import ListingForm
from property.models import Property  # assuming the property model is in the property app


# Listing List View (to list all listings)
@login_required
def listing_list(request):
    listings = Listing.objects.select_related('property').all()
    return render(request, 'listings/listing_list.html', {'listings': listings})

# Listing Create View (to create a new listing)
@login_required
def listing_create(request):
    if request.method == 'POST':
        form = ListingForm(request.POST, request.FILES)
        if form.is_valid():
            property = form.cleaned_data['property']

            # Ensure that the property belongs to the logged-in user
            if property.owner != request.user:
                return HttpResponseForbidden("You can only create listings for properties you own.")

            # Ensure that the property is approved before creating the listing
            if property.status != 'approved':
                return HttpResponseForbidden("You can only create listings for approved properties.")

            form.save()
            return redirect('listing_list')
    else:
        form = ListingForm()

    return render(request, 'listings/listing_form.html', {'form': form, 'action': 'Add'})


# Listing Update View (to update an existing listing)
@login_required
def listing_update(request, pk):
    listing = get_object_or_404(Listing, pk=pk)

    # Ensure that the listing belongs to the current user
    if listing.property.owner != request.user:
        return HttpResponseForbidden("You can only edit listings for properties you own.")

    if request.method == 'POST':
        form = ListingForm(request.POST, request.FILES, instance=listing)
        if form.is_valid():
            form.save()
            return redirect('listing_list')
    else:
        form = ListingForm(instance=listing)

    return render(request, 'listings/listing_form.html', {'form': form, 'action': 'Update'})


# Listing Delete View (to delete an existing listing)
@login_required
def listing_delete(request, pk):
    listing = get_object_or_404(Listing, pk=pk)

    # Ensure that the listing belongs to the current user
    if listing.property.owner != request.user:
        return HttpResponseForbidden("You can only delete listings for properties you own.")

    if request.method == 'POST':
        listing.delete()
        return redirect('listing_list')
    return render(request, 'listings/listing_confirm_delete.html', {'listing': listing})


# Tenant Listing View (to view listings for tenants based on university)
@login_required
def tenant_listing_view(request):
    user = request.user
    university = getattr(user, 'university', None)  # Assuming user model has university field
    listings = Listing.objects.select_related('property')

    # Match listings by university → property location
    if university:
        listings = listings.filter(property__location__icontains=university)

    # Apply search filters
    query = request.GET.get('q')
    if query:
        listings = listings.filter(
            Q(price__icontains=query) |
            Q(property__location__icontains=query)
        )

    return render(request, 'listings/tenants_listings.html', {'listings': listings, 'query': query})


from django.shortcuts import get_object_or_404

@login_required
def property_listings(request, property_id):
    property_obj = get_object_or_404(Property, id=property_id)
    listings = Listing.objects.select_related('property').filter(property=property_obj)
    return render(request, 'listings/listing_list.html', {
        'listings': listings,
        'property': property_obj
    })
