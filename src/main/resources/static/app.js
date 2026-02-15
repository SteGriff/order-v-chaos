let currentBattle = null;
let isVoting = false;

// Fetch current battle data
async function fetchCurrentBattle() {
    try {
        const response = await fetch('/api/current');
        const data = await response.json();
        currentBattle = data;
        updateUI();
    } catch (error) {
        console.error('Error fetching current battle:', error);
    }
}

// Fetch past battles
async function fetchPastBattles() {
    try {
        const response = await fetch('/api/past-battles');
        const battles = await response.json();
        renderPastBattles(battles);
    } catch (error) {
        console.error('Error fetching past battles:', error);
    }
}

// Submit a vote
async function submitVote(isLeft) {
    if (isVoting) return;
    
    isVoting = true;
    try {
        await fetch('/api/vote', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ isLeft })
        });
        
        // Immediately fetch updated scores
        await fetchCurrentBattle();
    } catch (error) {
        console.error('Error submitting vote:', error);
    } finally {
        isVoting = false;
    }
}

// Update the UI with current battle data
function updateUI() {
    if (!currentBattle) return;
    
    const { leftSide, rightSide, leftColour, rightColour, leftEmoji, rightEmoji, leftScore, rightScore } = currentBattle;
    
    // Update title
    document.getElementById('battle-title').textContent = `${leftSide} v ${rightSide}`;
    
    // Update battle bar
    const total = leftScore + rightScore || 1; // Avoid division by zero
    const leftPercent = (leftScore / total) * 100;
    const rightPercent = (rightScore / total) * 100;
    
    const leftBar = document.getElementById('left-bar');
    const rightBar = document.getElementById('right-bar');
    
    leftBar.style.flex = leftScore || 1;
    leftBar.style.backgroundColor = leftColour;
    
    rightBar.style.flex = rightScore || 1;
    rightBar.style.backgroundColor = rightColour;
    
    // Update scores
    document.getElementById('left-score').textContent = leftScore.toLocaleString();
    document.getElementById('right-score').textContent = rightScore.toLocaleString();
    
    // Update buttons
    const leftButton = document.getElementById('left-button');
    const rightButton = document.getElementById('right-button');
    
    leftButton.style.backgroundColor = leftColour;
    rightButton.style.backgroundColor = rightColour;
    
    document.getElementById('left-emoji').textContent = leftEmoji;
    document.getElementById('right-emoji').textContent = rightEmoji;
    
    document.getElementById('left-label').textContent = leftSide.toUpperCase();
    document.getElementById('right-label').textContent = rightSide.toUpperCase();
    
    // Enable buttons
    leftButton.disabled = false;
    rightButton.disabled = false;
}

// Render past battles list
function renderPastBattles(battles) {
    const container = document.getElementById('past-battles-list');
    
    if (battles.length === 0) {
        container.innerHTML = '<p class="loading">No past battles yet.</p>';
        return;
    }
    
    container.innerHTML = battles.map(battle => `
        <div class="past-battle">
            <div class="past-battle-date">${battle.date}</div>
            <div class="past-battle-title">${battle.leftSide} vs ${battle.rightSide}</div>
            <div class="past-battle-result">
                ${battle.leftSide} ${battle.leftPercentage.toFixed(1)}% - ${battle.rightPercentage.toFixed(1)}% ${battle.rightSide}
            </div>
        </div>
    `).join('');
}

// Set up event listeners
document.getElementById('left-button').addEventListener('click', () => submitVote(true));
document.getElementById('right-button').addEventListener('click', () => submitVote(false));

// Initial load
fetchCurrentBattle();
fetchPastBattles();

// Poll every 10 seconds
setInterval(fetchCurrentBattle, 10000);
