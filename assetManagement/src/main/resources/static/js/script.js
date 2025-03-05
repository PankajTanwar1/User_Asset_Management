
    document.getElementById('toggleButton').addEventListener('click', function() {
    // Select all elements with the class 'collapsedIcon'
    document.querySelectorAll('.collapsedIcon').forEach(function(icon) { 
        icon.classList.toggle('d-none');
      });
    });