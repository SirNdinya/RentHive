// Wait for DOM to be fully loaded before executing JavaScript
    document.addEventListener('DOMContentLoaded', function() {
      // Get references to form elements
      const roleSelect = document.querySelector('#roleSelect');
      const tenantFields = document.getElementById('tenant-fields');
      const propertyOwnerFields = document.getElementById('property-owner-fields');
      const universityField = document.getElementById('id_university');
      //const universityAreaField = document.getElementById('id_university_area');
      const phoneNumberField = document.getElementById('id_phone_number');

      // Function to show/hide fields based on selected role
      function toggleFields(role) {
        if (role === 'TENANT') {
          // Show tenant fields and make them required
          tenantFields.style.display = 'block';
          propertyOwnerFields.style.display = 'none';
          universityField.required = true;
          //universityAreaField.required = false;
          phoneNumberField.required = false;
        }
        else if (role === 'PROPERTY_OWNER') {
          // Show property owner fields and make them required
          tenantFields.style.display = 'none';
          propertyOwnerFields.style.display = 'block';
         // universityAreaField.required = true;
          phoneNumberField.required = true;
          universityField.required = false;
        }
        else {
          // Hide all role-specific fields if no role is selected
          tenantFields.style.display = 'none';
          propertyOwnerFields.style.display = 'none';
          universityField.required = false;
         // universityAreaField.required = false;
          phoneNumberField.required = false;
        }
      }

      // Initialize fields based on current selection
      toggleFields(roleSelect.value);

      // Add event listener for role selection changes
      roleSelect.addEventListener('change', function() {
        toggleFields(this.value);  // 'this' refers to the select element
      });
    });


    document.addEventListener('DOMContentLoaded', function() {
            const messages = document.querySelectorAll('.message');

            // Add click handler for close buttons
            document.querySelectorAll('.close-btn').forEach(btn => {
                btn.addEventListener('click', function() {
                    fadeOut(this.parentElement);
                });
            });

            // Auto-hide all messages after 5 seconds
            messages.forEach(msg => {
                setTimeout(() => {
                    fadeOut(msg);
                }, 5000);
            });

            function fadeOut(element) {
                element.style.transition = 'opacity 0.5s ease-out';
                element.style.opacity = '0';
                setTimeout(() => {
                    element.style.display = 'none';
                }, 500);
            }
        });

  document.addEventListener("DOMContentLoaded", function () {
    const inputField = document.getElementById("id_university");
    const tenantFields = document.getElementById("tenant-fields"); // Get the parent container
    const suggestionsBox = document.createElement("div");
    suggestionsBox.id = "suggestions";
    suggestionsBox.classList.add("suggestions");
    tenantFields.appendChild(suggestionsBox); // Append to the tenant-fields container instead of body

    // Debounce function to limit API calls
    let debounceTimer;
    function debounce(callback, delay) {
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(callback, delay);
    }

    inputField.addEventListener("input", function () {
        const query = inputField.value.trim();

        // Clear previous suggestions
        suggestionsBox.innerHTML = "";
        suggestionsBox.style.display = "none";

        if (query.length > 1) {
            debounce(() => {
                fetch(`/user/university-suggestions/?q=${encodeURIComponent(query)}`)
                    .then(response => {
                        if (!response.ok) throw new Error('Network response was not ok');
                        return response.json();
                    })
                    .then(data => {
                        suggestionsBox.innerHTML = "";
                        if (data.length > 0) {
                            data.forEach(item => {
                                const div = document.createElement("div");
                                div.textContent = item;
                                div.classList.add("suggestion-item");
                                div.addEventListener("click", () => {
                                    inputField.value = item;
                                    suggestionsBox.style.display = "none";
                                });
                                suggestionsBox.appendChild(div);
                            });
                            adjustSuggestionBoxPosition();
                            suggestionsBox.style.display = "block";
                        }
                    })
                    .catch(error => {
                        console.error('Fetch error:', error);
                        suggestionsBox.style.display = "none";
                    });
            }, 300); // 300ms delay
        }
    });

    // Adjust the position of the suggestion box dynamically
    function adjustSuggestionBoxPosition() {
        const inputRect = inputField.getBoundingClientRect();
        const viewportHeight = window.innerHeight;
        const spaceBelow = viewportHeight - inputRect.bottom;
        const suggestionBoxHeight = Math.min(200, suggestionsBox.scrollHeight);

        // Reset styles
        suggestionsBox.style.top = "";
        suggestionsBox.style.bottom = "";
        suggestionsBox.style.maxHeight = "";
        suggestionsBox.style.transform = "";

        if (spaceBelow < suggestionBoxHeight && inputRect.top > suggestionBoxHeight) {
            // Not enough space below, but enough space above - position above
            suggestionsBox.style.bottom = "100%";
            suggestionsBox.style.maxHeight = `${Math.min(200, inputRect.top - 10)}px`;
        } else {
            // Default position below
            suggestionsBox.style.top = "100%";
            suggestionsBox.style.maxHeight = `${Math.min(200, spaceBelow - 10)}px`;
        }
    }

    // Hide suggestions when clicking outside
    document.addEventListener("click", function (e) {
        if (!inputField.contains(e.target) && !suggestionsBox.contains(e.target)) {
            suggestionsBox.style.display = "none";
        }
    });

    // Keyboard navigation support
    inputField.addEventListener("keydown", function (e) {
        if (suggestionsBox.style.display !== "block") return;

        const items = suggestionsBox.querySelectorAll(".suggestion-item");
        let currentItem = document.querySelector(".suggestion-item.highlighted");

        if (e.key === "ArrowDown") {
            e.preventDefault();
            if (!currentItem) {
                items[0]?.classList.add("highlighted");
            } else {
                currentItem.classList.remove("highlighted");
                const nextItem = currentItem.nextElementSibling || items[0];
                nextItem.classList.add("highlighted");
                nextItem.scrollIntoView({ block: "nearest" });
            }
        } else if (e.key === "ArrowUp") {
            e.preventDefault();
            if (currentItem) {
                currentItem.classList.remove("highlighted");
                const prevItem = currentItem.previousElementSibling || items[items.length - 1];
                prevItem.classList.add("highlighted");
                prevItem.scrollIntoView({ block: "nearest" });
            }
        } else if (e.key === "Enter" && currentItem) {
            e.preventDefault();
            inputField.value = currentItem.textContent;
            suggestionsBox.style.display = "none";
        }
    });

    // Adjust position on window resize and scroll
    window.addEventListener("resize", adjustSuggestionBoxPosition);
    window.addEventListener("scroll", adjustSuggestionBoxPosition);
});