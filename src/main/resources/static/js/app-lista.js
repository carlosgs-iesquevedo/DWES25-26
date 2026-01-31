document.addEventListener('DOMContentLoaded', () =>{

    const table = document.querySelector('#listaTarjetas');
    if (!table) return; // evitar errores si no existe la tabla

    table.addEventListener('click', async (event) => {

        const link = event.target.closest('a');

        // sólo manejar enlaces con la clase exacta 'borrarTarjetaLink'
        const isDelete = link.classList.contains('borrarTarjetaLink');
        if (!isDelete) return;

        event.preventDefault();

        const tr = link.closest('tr');
        const idEl = tr && tr.querySelector('.tarjetaId');
        const id = idEl ? idEl.textContent.trim() : null;

        const url = "/admin/tarjetas/" + id + "/delete/confirm";
        try {
            const response = await fetch(url);
            if (!response.ok) throw new Error(`Response status: ${response.status}`);
            const html = await response.text();
            document.querySelector('#placeholder-modal').innerHTML = html;

            const modalEl = document.querySelector('#delete-modal');
            if (modalEl) {
                const modal = new bootstrap.Modal(modalEl);
                modal.show();
            } else {
                console.error('Modal no encontrado en el HTML recibido');
            }
        } catch (error) {
            console.error(error.message);
        }

    })

});
