/**
 * EMS Frontend Validation Library
 * Pure JS — no framework dependency.
 * Usage: attach data-* attributes on inputs, call emsValidate(form) on submit.
 */

// ── Core validator ────────────────────────────────────────────────────────────
(function () {
  'use strict';

  // Show an error below the field
  function showErr(field, msg) {
    field.classList.add('fc-err');
    let span = field.parentElement.querySelector('.v-err');
    if (!span) {
      span = document.createElement('span');
      span.className = 'v-err';
      field.parentElement.appendChild(span);
    }
    span.textContent = msg;
  }

  // Clear error on a field
  function clearErr(field) {
    field.classList.remove('fc-err');
    const span = field.parentElement.querySelector('.v-err');
    if (span) span.remove();
  }

  // Validate one field, return true if valid
  function validateField(field) {
    const val = field.value.trim();
    const rules = field.dataset;

    // Required
    if (field.required && val === '') {
      const label = field.closest('.fi')?.querySelector('label')?.textContent?.replace('*','').trim() || 'This field';
      showErr(field, label + ' is required');
      return false;
    }

    if (val === '') { clearErr(field); return true; } // optional empty = ok

    // Letters only (names)
    if (rules.letters !== undefined) {
      if (!/^[a-zA-Z\s]+$/.test(val)) {
        showErr(field, rules.lettersMsg || 'Only letters and spaces are allowed');
        return false;
      }
    }

    // Letters + spaces + dot (person names with initials)
    if (rules.name !== undefined) {
      if (!/^[a-zA-Z\s.'-]+$/.test(val)) {
        showErr(field, 'Name can only contain letters, spaces, . \' -');
        return false;
      }
      if (val.length < 2) { showErr(field, 'Must be at least 2 characters'); return false; }
    }

    // Phone — exactly 10 digits
    if (rules.phone !== undefined) {
      if (!/^[0-9]{10}$/.test(val)) {
        showErr(field, 'Phone number must be exactly 10 digits');
        return false;
      }
    }

    // Email
    if (field.type === 'email' || rules.email !== undefined) {
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val)) {
        showErr(field, 'Enter a valid email address');
        return false;
      }
    }

    // Min length
    if (rules.minlen && val.length < parseInt(rules.minlen)) {
      showErr(field, rules.minlenMsg || 'Must be at least ' + rules.minlen + ' characters');
      return false;
    }

    // Max length
    if (rules.maxlen && val.length > parseInt(data.maxlen)) {
      showErr(field, 'Cannot exceed ' + data.maxlen + ' characters');
      return false;
    }

    // Numeric min
    if (rules.nummin !== undefined) {
      const n = parseFloat(val);
      if (isNaN(n) || n < parseFloat(rules.nummin)) {
        showErr(field, rules.numminMsg || 'Must be at least ' + rules.nummin);
        return false;
      }
    }

    // Username — alphanumeric + dot + underscore
    if (rules.username !== undefined) {
      if (!/^[a-zA-Z0-9._]{3,30}$/.test(val)) {
        showErr(field, 'Username: 3–30 chars, letters, numbers, dot or underscore only');
        return false;
      }
    }

    // Password min length
    if (rules.password !== undefined) {
      if (val.length < 6) {
        showErr(field, 'Password must be at least 6 characters');
        return false;
      }
    }

    // Confirm password match
    if (rules.confirm !== undefined) {
      const original = document.querySelector('[name="' + rules.confirm + '"]');
      if (original && original.value !== field.value) {
        showErr(field, 'Passwords do not match');
        return false;
      }
    }

    // Date not in past (for leave from date)
    if (rules.notpast !== undefined) {
      const today = new Date(); today.setHours(0,0,0,0);
      const picked = new Date(val);
      if (picked < today) {
        showErr(field, 'Date cannot be in the past');
        return false;
      }
    }

    // To-date must be >= from-date
    if (rules.afterfield !== undefined) {
      const other = document.querySelector('[name="' + rules.afterfield + '"]');
      if (other && other.value && new Date(val) < new Date(other.value)) {
        showErr(field, 'End date must be on or after start date');
        return false;
      }
    }

    // Positive number
    if (rules.positive !== undefined) {
      const n = parseFloat(val);
      if (isNaN(n) || n <= 0) {
        showErr(field, rules.positiveMsg || 'Must be a positive number');
        return false;
      }
    }

    // Year range
    if (rules.year !== undefined) {
      const n = parseInt(val);
      if (isNaN(n) || n < 2000 || n > 2100) {
        showErr(field, 'Enter a valid year (2000–2100)');
        return false;
      }
    }

    clearErr(field);
    return true;
  }

  // ── Public API ──────────────────────────────────────────────────────────────

  // Validate entire form — returns true if all fields pass
  window.emsValidate = function (form) {
    let valid = true;
    form.querySelectorAll('input, textarea, select').forEach(function (f) {
      if (!validateField(f)) valid = false;
    });
    // Scroll to first error
    const first = form.querySelector('.fc-err');
    if (first) first.scrollIntoView({ behavior: 'smooth', block: 'center' });
    return valid;
  };

  // Live validation — clear errors as user fixes them
  window.emsLive = function (form) {
    form.querySelectorAll('input, textarea, select').forEach(function (f) {
      f.addEventListener('input', function () { validateField(f); });
      f.addEventListener('blur',  function () { validateField(f); });
      f.addEventListener('change', function () { validateField(f); });
    });
  };

})();
