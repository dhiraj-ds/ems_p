// ── Sidebar ──────────────────────────────────────────────────────────────────
function toggleSidebar(){const sb=document.getElementById('sidebar'),mc=document.getElementById('main');if(sb)sb.classList.toggle('off');if(mc)mc.classList.toggle('wide');}
function toggleSub(btn){btn.classList.toggle('ex');const sub=btn.nextElementSibling;if(sub&&sub.classList.contains('sub'))sub.classList.toggle('open');}

// ── Delete modal ─────────────────────────────────────────────────────────────
let _df=null;
function openDel(name,formId){const el=document.getElementById('delName');if(el)el.textContent=name;_df=document.getElementById(formId);const m=document.getElementById('delModal');if(m)m.classList.add('show');}
function closeDel(){const m=document.getElementById('delModal');if(m)m.classList.remove('show');}
function confirmDel(){if(_df)_df.submit();closeDel();}

// ── Payroll ───────────────────────────────────────────────────────────────────
function calcNet(){const b=parseFloat(document.getElementById('basicSalary')?.value||0),a=parseFloat(document.getElementById('allowances')?.value||0),d=parseFloat(document.getElementById('deductions')?.value||0),el=document.getElementById('netPreview');if(el)el.textContent='₹'+(b+a-d).toLocaleString('en-IN',{maximumFractionDigits:2});}
function autoFill(sel){const o=sel.options[sel.selectedIndex],s=o.getAttribute('data-salary')||0,f=document.getElementById('basicSalary');if(f){f.value=s;calcNet();}}
function printSlip(){window.print();}

// ── DOM Ready ─────────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded',()=>{

  // Auto-dismiss alerts after 4s
  setTimeout(()=>{document.querySelectorAll('.alert').forEach(el=>{el.style.transition='opacity .5s';el.style.opacity='0';setTimeout(()=>el.remove(),500);});},4000);

  // Mobile sidebar collapsed by default
  if(window.innerWidth<768){
    const sb=document.getElementById('sidebar'),mc=document.getElementById('main');
    if(sb)sb.classList.add('off'); if(mc)mc.classList.add('wide');
  }

  // Today's date chip
  const el=document.getElementById('todayDate');
  if(el)el.textContent=new Date().toLocaleDateString('en-IN',{weekday:'long',year:'numeric',month:'long',day:'numeric'});

  // ── JWT session management ─────────────────────────────────────────────────
  // Access token expires in 30 min. The filter auto-refreshes it transparently
  // via the refresh token on every page navigation. But for long-lived single
  // pages (e.g. filling a form for 25 min), we silently ping /auth/refresh
  // at 24 min to get fresh cookies before the access token dies.
  const ACCESS_MS  = 30 * 60 * 1000; // 30 min
  const REFRESH_AT = 24 * 60 * 1000; // ping at 24 min (6 min before expiry)
  const WARN_AT    = 29 * 60 * 1000; // warn at 29 min if ping failed

  // Silent background refresh at 24 min — fetch /auth/refresh so the filter
  // can issue new cookies. User stays on the current page uninterrupted.
  setTimeout(async ()=>{
    try {
      const r = await fetch('/auth/refresh', {method:'GET',credentials:'include',redirect:'follow'});
      if(r.ok || r.redirected){
        console.log('[EMS] Session refreshed silently at 24 min');
      }
    } catch(e){
      console.warn('[EMS] Silent refresh failed:', e);
    }
  }, REFRESH_AT);

  // Visible warning at 29 min — if the silent refresh failed, warn the user
  // so they can save work before being redirected to login
  setTimeout(()=>{
    // Only show if the page is still active (tab not in background sleeping)
    const b=document.createElement('div');
    b.className='alert al-wn';
    b.style.cssText='position:fixed;top:70px;right:20px;z-index:9999;max-width:340px;box-shadow:0 4px 20px rgba(0,0,0,.18);cursor:pointer;';
    b.innerHTML='&#x23F1; <strong>Session expiring soon.</strong> <a href="/auth/refresh" style="color:inherit;font-weight:700;text-decoration:underline;">Click to extend</a> or save your work.';
    b.onclick=()=>b.remove();
    document.body.appendChild(b);
    setTimeout(()=>b.remove(), 60*1000); // remove after 1 min
  }, WARN_AT);

  // Auto-print payslip download
  if(document.getElementById('autoDownloadFlag')) window.print();

  // Payroll net calc on load
  calcNet();
});
