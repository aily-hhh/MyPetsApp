package com.hhh.mypetsapp.ui.surgical;

import androidx.cardview.widget.CardView;

public interface SurgicalProceduresClickListener {
    void onClick(SurgicalProcedures surgicalProcedures);
    void onLongClick(SurgicalProcedures surgicalProcedures, CardView cardView);
}
