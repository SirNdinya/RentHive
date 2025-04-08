// Wait for DOM to be fully loaded before executing JavaScript
document.addEventListener('DOMContentLoaded', function() {
  // Get references to form elements
  const roleSelect = document.querySelector('#roleSelect'); // Role selection dropdown
  const tenantFields = document.getElementById('tenant-fields'); // Tenant-specific fields container
  const propertyOwnerFields = document.getElementById('property-owner-fields'); // Property owner-specific fields container
  const universityField = document.getElementById('id_university'); // University input field
  const phoneNumberField = document.getElementById('id_phone_number'); // Phone number input field

  // Function to show/hide fields based on selected role
  function toggleFields(role) {
    if (role === 'TENANT') {
      // Show tenant fields and make them required
      tenantFields.style.display = 'block';
      propertyOwnerFields.style.display = 'none';
      universityField.required = true; // Set university field as required
      phoneNumberField.required = false; // Phone number is not required for tenant
    } else if (role === 'PROPERTY_OWNER') {
      // Show property owner fields and make them required
      tenantFields.style.display = 'none';
      propertyOwnerFields.style.display = 'block';
      phoneNumberField.required = true; // Phone number is required for property owner
      universityField.required = false; // University field is not required for property owner
    } else {
      // Hide all role-specific fields if no role is selected
      tenantFields.style.display = 'none';
      propertyOwnerFields.style.display = 'none';
      universityField.required = false; // University field is not required if no role is selected
      phoneNumberField.required = false; // Phone number field is not required if no role is selected
    }
  }

  // Initialize fields based on current selection
  toggleFields(roleSelect.value);

  // Add event listener for role selection changes
  roleSelect.addEventListener('change', function() {
    toggleFields(this.value);  // 'this' refers to the select element
  });
});

// Handle message display and auto-hide after 5 seconds
document.addEventListener('DOMContentLoaded', function() {
  const messages = document.querySelectorAll('.message'); // Get all message elements

  // Add click handler for close buttons
  document.querySelectorAll('.close-btn').forEach(btn => {
    btn.addEventListener('click', function() {
      fadeOut(this.parentElement); // Fade out the parent message element when the close button is clicked
    });
  });

  // Auto-hide all messages after 5 seconds
  messages.forEach(msg => {
    setTimeout(() => {
      fadeOut(msg); // Fade out each message element after 5 seconds
    }, 5000);
  });

  // Function to fade out an element
  function fadeOut(element) {
    element.style.transition = 'opacity 0.5s ease-out'; // Apply a fade-out transition
    element.style.opacity = '0'; // Set opacity to 0 to make it invisible
    setTimeout(() => {
      element.style.display = 'none'; // After fade-out, set display to 'none' to remove from the layout
    }, 500); // Delay of 0.5 seconds to match transition duration
  }
});

// Handle university suggestions based on input and manage debounce
document.addEventListener("DOMContentLoaded", function () {
  const inputField = document.getElementById("id_university"); // Get the university input field
  const tenantFields = document.getElementById("tenant-fields"); // Get the parent container for tenant fields
  const suggestionsBox = document.createElement("div"); // Create a new div to display suggestions
  suggestionsBox.id = "suggestions"; // Set an ID for the suggestions box
  suggestionsBox.classList.add("suggestions"); // Add a class for styling
  tenantFields.appendChild(suggestionsBox); // Append the suggestions box to the tenant fields container

  // Debounce function to limit the frequency of API calls
  let debounceTimer;
  function debounce(callback, delay) {
    clearTimeout(debounceTimer); // Clear the previous timer
    debounceTimer = setTimeout(callback, delay); // Set a new timer to call the callback after a delay
  }

  // Listen for input events on the university input field
  inputField.addEventListener("input", function () {
    const query = inputField.value.trim(); // Get the trimmed value of the input field

    // Clear previous suggestions and hide suggestions box
    suggestionsBox.innerHTML = "";
    suggestionsBox.style.display = "none";

    if (query.length > 1) {
      debounce(() => {
        // Fetch suggestions if the query length is greater than 1
        fetch(`/user/university-suggestions/?q=${encodeURIComponent(query)}`)
          .then(response => {
            if (!response.ok) throw new Error('Network response was not ok');
            return response.json();
          })
          .then(data => {
            suggestionsBox.innerHTML = ""; // Clear any existing suggestions
            if (data.length > 0) {
              // Display new suggestions if data is returned
              data.forEach(item => {
                const div = document.createElement("div"); // Create a new div for each suggestion
                div.textContent = item; // Set the text of the div to the suggestion item
                div.classList.add("suggestion-item"); // Add a class for styling
                div.addEventListener("click", () => {
                  inputField.value = item; // Set the input field value to the selected suggestion
                  suggestionsBox.style.display = "none"; // Hide the suggestions box
                });
                suggestionsBox.appendChild(div); // Append the suggestion item to the suggestions box
              });
              adjustSuggestionBoxPosition(); // Adjust the position of the suggestions box
              suggestionsBox.style.display = "block"; // Display the suggestions box
            }
          })
          .catch(error => {
            console.error('Fetch error:', error);
            suggestionsBox.style.display = "none"; // Hide the suggestions box on error
          });
      }, 300); // 300ms delay before making the API call (debounce)
    }
  });

  // Function to adjust the position of the suggestions box dynamically
  function adjustSuggestionBoxPosition() {
    const inputRect = inputField.getBoundingClientRect(); // Get the position of the input field
    const viewportHeight = window.innerHeight; // Get the height of the viewport
    const spaceBelow = viewportHeight - inputRect.bottom; // Calculate the space available below the input field
    const suggestionBoxHeight = Math.min(200, suggestionsBox.scrollHeight); // Set the max height of the suggestions box

    // Reset positioning styles
    suggestionsBox.style.top = "";
    suggestionsBox.style.bottom = "";
    suggestionsBox.style.maxHeight = "";
    suggestionsBox.style.transform = "";

    if (spaceBelow < suggestionBoxHeight && inputRect.top > suggestionBoxHeight) {
      // If not enough space below, position the box above the input field
      suggestionsBox.style.bottom = "100%";
      suggestionsBox.style.maxHeight = `${Math.min(200, inputRect.top - 10)}px`;
    } else {
      // Position the box below the input field by default
      suggestionsBox.style.top = "100%";
      suggestionsBox.style.maxHeight = `${Math.min(200, spaceBelow - 10)}px`;
    }
  }

  // Hide suggestions when clicking outside the input field or suggestions box
  document.addEventListener("click", function (e) {
    if (!inputField.contains(e.target) && !suggestionsBox.contains(e.target)) {
      suggestionsBox.style.display = "none"; // Hide suggestions if clicked outside
    }
  });

  // Handle keyboard navigation within the suggestions box
  inputField.addEventListener("keydown", function (e) {
    if (suggestionsBox.style.display !== "block") return; // Exit if suggestions are not displayed

    const items = suggestionsBox.querySelectorAll(".suggestion-item"); // Get all suggestion items
    let currentItem = document.querySelector(".suggestion-item.highlighted"); // Get the currently highlighted suggestion

    if (e.key === "ArrowDown") {
      e.preventDefault(); // Prevent default behavior (scrolling)
      if (!currentItem) {
        items[0]?.classList.add("highlighted"); // Highlight the first item if none is highlighted
      } else {
        currentItem.classList.remove("highlighted"); // Remove highlight from the current item
        const nextItem = currentItem.nextElementSibling || items[0]; // Get the next item (or wrap around to the first)
        nextItem.classList.add("highlighted"); // Highlight the next item
        nextItem.scrollIntoView({ block: "nearest" }); // Scroll to the next item
      }
    } else if (e.key === "ArrowUp") {
      e.preventDefault(); // Prevent default behavior (scrolling)
      if (currentItem) {
        currentItem.classList.remove("highlighted"); // Remove highlight from the current item
        const prevItem = currentItem.previousElementSibling || items[items.length - 1]; // Get the previous item (or wrap around to the last)
        prevItem.classList.add("highlighted"); // Highlight the previous item
        prevItem.scrollIntoView({ block: "nearest" }); // Scroll to the previous item
      }
    } else if (e.key === "Enter" && currentItem) {
      e.preventDefault(); // Prevent default form submission behavior
      inputField.value = currentItem.textContent; // Set the input field value to the selected item
      suggestionsBox.style.display = "none"; // Hide the suggestions box
    }
  });

  // Adjust position of the suggestions box when window is resized or scrolled
  window.addEventListener("resize", adjustSuggestionBoxPosition);
  window.addEventListener("scroll", adjustSuggestionBoxPosition);
});
