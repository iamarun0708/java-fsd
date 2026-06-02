console.log("Welcome to the Community Portal");

function Event(id, name, date, seats, category) {
    this.id = id;
    this.name = name;
    this.date = date;
    this.seats = seats;
    this.category = category;
}

Event.prototype.checkAvailability = function() {
    return this.seats > 0;
};

let eventsList = [];
const trackCategoryRegistration = (function() {
    const counts = {};
    return function(category) {
        counts[category] = (counts[category] || 0) + 1;
        return counts[category];
    };
})();

window.addEventListener("load", () => {
    alert("Page is fully loaded!");
    loadEvents();
});

async function loadEvents() {
    const spinner = document.querySelector("#loadingSpinner");
    if (spinner) spinner.style.display = "block";

    try {
        const response = await fetch("events.json");
        if (!response.ok) throw new Error("Failed to fetch events.");
        const data = await response.json();
        
        eventsList = data.map(item => new Event(item.id, item.name, item.date, item.seats, item.category));
        renderEvents(eventsList);
    } catch (error) {
        console.error(error);
        const container = document.querySelector("#eventsContainer");
        if (container) container.innerHTML = "<p class='text-danger'>Error loading events.</p>";
    } finally {
        if (spinner) spinner.style.display = "none";
    }
}

function renderEvents(list) {
    const container = document.querySelector("#eventsContainer");
    if (!container) return;
    container.innerHTML = "";

    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const formattedCards = list.map(event => {
        const { id, name, date, seats, category } = event;
        const eventDate = new Date(date);
        
        let statusText = "";
        let isPast = eventDate < today;
        let isFull = seats <= 0;

        if (isPast || isFull) {
            return null;
        }

        const entries = Object.entries(event);
        let entriesHtml = "";
        entries.forEach(([key, val]) => {
            if (key !== "id") {
                entriesHtml += `<li><strong>${key}:</strong> ${val}</li>`;
            }
        });

        return `
            <div class="col-md-6 mb-3 event-card" id="event-card-${id}">
                <div class="card h-100">
                    <div class="card-body">
                        <h3 class="card-title h5">${name}</h3>
                        <p class="card-text text-muted">Category: ${category} | Date: ${date}</p>
                        <p class="card-text">Seats Available: <span id="seats-count-${id}">${seats}</span></p>
                        <ul>${entriesHtml}</ul>
                        <button class="btn btn-primary btn-sm register-btn" onclick="handleRegistration(${id})">Register</button>
                    </div>
                </div>
            </div>
        `;
    });

    formattedCards.forEach(cardHtml => {
        if (cardHtml) {
            const div = document.createElement("div");
            div.innerHTML = cardHtml;
            container.appendChild(div.firstElementChild);
        }
    });
}

function handleRegistration(eventId) {
    try {
        const event = eventsList.find(e => e.id === eventId);
        if (!event) throw new Error("Event not found.");
        
        if (!event.checkAvailability()) {
            throw new Error("No seats available for this event.");
        }

        event.seats--;
        const seatsEl = document.querySelector(`#seats-count-${eventId}`);
        if (seatsEl) seatsEl.textContent = event.seats;

        const catRegCount = trackCategoryRegistration(event.category);
        alert(`Successfully registered for ${event.name}!\nTotal registrations for ${event.category}: ${catRegCount}`);

        if (event.seats === 0) {
            const card = document.querySelector(`#event-card-${eventId}`);
            if (card) {
                $(card).fadeOut(500, () => {
                    renderEvents(eventsList);
                });
            }
        }
    } catch (error) {
        alert(`Registration Error: ${error.message}`);
    }
}

function filterEvents(category) {
    const listClone = [...eventsList];
    if (!category) {
        renderEvents(listClone);
        return;
    }
    const filtered = listClone.filter(e => e.category === category);
    renderEvents(filtered);
}

function searchEvents(query) {
    const listClone = [...eventsList];
    const filtered = listClone.filter(e => e.name.toLowerCase().includes(query.toLowerCase()));
    renderEvents(filtered);
}

const regForm = document.querySelector("#registrationForm");
if (regForm) {
    regForm.addEventListener("submit", (event) => {
        event.preventDefault();
        
        const elements = regForm.elements;
        const name = elements["userName"].value.trim();
        const email = elements["userEmail"].value.trim();
        const selectedEventId = elements["selectedEvent"].value;

        const errorEl = document.querySelector("#formErrors");
        if (errorEl) errorEl.innerHTML = "";

        let errors = [];
        if (!name) errors.push("Name is required.");
        if (!email) errors.push("Email is required.");
        if (!selectedEventId) errors.push("Please select an event.");

        if (errors.length > 0) {
            if (errorEl) {
                errorEl.innerHTML = errors.map(err => `<div class='text-danger'>${err}</div>`).join("");
            }
            return;
        }

        const submitBtn = regForm.querySelector("button[type='submit']");
        if (submitBtn) submitBtn.disabled = true;

        const payload = { name, email, eventId: selectedEventId };

        fetch("https://jsonplaceholder.typicode.com/posts", {
            method: "POST",
            body: JSON.stringify(payload),
            headers: {
                "Content-type": "application/json; charset=UTF-8"
            }
        })
        .then(response => {
            if (!response.ok) throw new Error("Server error.");
            return response.json();
        })
        .then(data => {
            setTimeout(() => {
                alert("Registration submitted successfully to server!");
                regForm.reset();
                if (submitBtn) submitBtn.disabled = false;
            }, 1000);
        })
        .catch(err => {
            alert(`Error: ${err.message}`);
            if (submitBtn) submitBtn.disabled = false;
        });
    });
}

// Benefit of React/Vue: They utilize a virtual DOM and declarative state management, which updates the UI automatically when data changes, eliminating manual DOM queries and manipulations.
