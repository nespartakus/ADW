import type { DiagnosticAnalysis, Hypothesis } from '@/lib/api'

const CONFIDENCE_STYLE: Record<Hypothesis['confidence'], string> = {
  HIGH: 'text-success',
  MEDIUM: 'text-warning',
  LOW: 'text-fg-muted',
}

const CONFIDENCE_LABEL: Record<Hypothesis['confidence'], string> = {
  HIGH: 'Alta',
  MEDIUM: 'Media',
  LOW: 'Baja',
}

export function AnalysisView({ analysis }: { analysis: DiagnosticAnalysis }) {
  return (
    <div className="flex flex-col gap-4 text-sm">
      {analysis.safetyWarnings.length > 0 && (
        <div className="rounded-md border border-warning/30 bg-warning/10 px-3 py-2">
          <div className="mb-1 font-medium text-warning">Advertencias de seguridad</div>
          <ul className="list-disc pl-5 text-warning">
            {analysis.safetyWarnings.map((w, i) => (
              <li key={i}>{w}</li>
            ))}
          </ul>
        </div>
      )}

      {analysis.hypotheses.length > 0 && (
        <section>
          <h3 className="mb-2 text-xs font-medium uppercase tracking-wide text-fg-muted">Hipótesis</h3>
          <div className="flex flex-col gap-2">
            {analysis.hypotheses.map((h) => (
              <div key={h.id} className="rounded-md border border-border bg-graphite px-3 py-2">
                <div className="flex items-center justify-between">
                  <span className="font-medium">{h.title}</span>
                  <span className={`text-xs font-medium ${CONFIDENCE_STYLE[h.confidence]}`}>
                    Confianza {CONFIDENCE_LABEL[h.confidence]}
                  </span>
                </div>
                {h.supportingEvidence.length > 0 && (
                  <div className="mt-1 text-fg-muted">
                    A favor: {h.supportingEvidence.join(', ')}
                  </div>
                )}
                {h.contradictingEvidence.length > 0 && (
                  <div className="mt-1 text-fg-muted">
                    En contra: {h.contradictingEvidence.join(', ')}
                  </div>
                )}
              </div>
            ))}
          </div>
        </section>
      )}

      {analysis.missingInformation.length > 0 && (
        <section>
          <h3 className="mb-2 text-xs font-medium uppercase tracking-wide text-fg-muted">
            Información faltante
          </h3>
          <ul className="list-disc pl-5 text-fg-muted">
            {analysis.missingInformation.map((m, i) => (
              <li key={i}>{m.question}</li>
            ))}
          </ul>
        </section>
      )}

      {analysis.dtcs.length > 0 && (
        <section>
          <h3 className="mb-2 text-xs font-medium uppercase tracking-wide text-fg-muted">DTC</h3>
          <div className="flex flex-wrap gap-2">
            {analysis.dtcs.map((d, i) => (
              <span key={i} className="rounded border border-border px-2 py-1 font-mono text-xs">
                {d.code} · {d.status}
              </span>
            ))}
          </div>
        </section>
      )}

      {analysis.testPlan.length > 0 && (
        <section>
          <h3 className="mb-2 text-xs font-medium uppercase tracking-wide text-fg-muted">
            Plan de pruebas
          </h3>
          <ol className="list-decimal pl-5 text-fg-muted">
            {analysis.testPlan.map((t) => (
              <li key={t.id}>{t.title}</li>
            ))}
          </ol>
        </section>
      )}

      <div className="text-xs text-fg-muted">
        Estado de conclusión: <span className="font-mono">{analysis.conclusionStatus}</span>
      </div>
    </div>
  )
}
