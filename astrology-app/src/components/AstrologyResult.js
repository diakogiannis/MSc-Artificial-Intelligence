import React from 'react';
import { Container, Card, Row, Col, ListGroup, Button, Table, Alert } from 'react-bootstrap';
import parse from 'html-react-parser';
import {AstrologyResultMapImg}  from '../utils/svgUtils';

const SectionCard = ({ title, children }) => (
  <Card className="mb-4 shadow-sm">
    <Card.Body>
      <Card.Title as="h4" className="mb-3">{title}</Card.Title>
      {children}
    </Card.Body>
  </Card>
);

const HtmlContent = ({ content }) => {
  if (!content) return null;
  return <div>{parse(content)}</div>;
}; 


function AstrologyResult({ uiData, onReset }) {
  if (!uiData) {
    return (
      <Container className="mt-5">
        <Alert variant="warning">Δεν βρέθηκαν δεδομένα για εμφάνιση.</Alert>
        <Button onClick={onReset} variant="secondary">Επιστροφή</Button>
      </Container>
    );
  }

  const {
    birthData, ascendant, personalitySummary, chartSvg, planets,
    yearlyForecast, careerGuidance, finalOverview, yearPrediction
  } = uiData;


  return (
    <Container className="my-4">
      <div className="text-center mb-4">
        <h1>Αστρολογική Ανάλυση</h1>
        <Button onClick={onReset} variant="outline-primary">← Νέα Αναζήτηση</Button>
      </div>

      <SectionCard title="Στοιχεία Γέννησης">
        <p><strong>Ημερομηνία & Ώρα:</strong> {birthData.text}</p>
        <p><strong>Τοποθεσία:</strong> {birthData.location}</p>
        <p><strong>Ζώδιο: </strong> {birthData.zodiac_sign}</p>
      </SectionCard>

      <SectionCard title="Σύνοψη Προσωπικότητας">
        <p className="text-muted"><HtmlContent content={personalitySummary} /></p>
      </SectionCard>

      <SectionCard title="Ωροσκόπος (Ascendant)">
        <h5>{ascendant.title}</h5>
        <p className="text-muted">
          <HtmlContent content={ascendant.interpretation} />
        </p>
        <p className="text-muted">
          <HtmlContent content={ascendant.calculation_explanation} />
        </p>
      </SectionCard>

      <SectionCard title="Γενέθλιος Χάρτης">
        {chartSvg ? (
          <AstrologyResultMapImg report={chartSvg} />
        ) : (
          <Alert variant="info">Ο γενέθλιος χάρτης δεν είναι διαθέσιμος.</Alert>
        )}
      </SectionCard>

 


      <SectionCard title="Πλανητικές Θέσεις">
        <Table striped bordered hover responsive>
          <thead>
            <tr>
              <th>Πλανήτης</th>
              <th>Ζώδιο</th>
              <th>Οίκος</th>
              <th>Ερμηνεία</th>
            </tr>
          </thead>
          <tbody>
            {planets.map((p) => (
              <tr key={p.name}>
                <td><strong>{p.name}</strong></td>
                <td>{p.sign}</td>
                <td>{p.house}</td>
                <td><p><HtmlContent content={p.interpretation} /></p>
                <p>
                  <strong>Τεχνική Ερμηνεία</strong>
                </p>
                <p><HtmlContent content={p.technical_interpretation} /></p>
                </td>
                
              </tr>
            ))}
          </tbody>
        </Table>
      </SectionCard>

      <SectionCard title={yearlyForecast.title}>
        <h6>Γενική Τάση</h6>
        <p><HtmlContent content={yearlyForecast.summary} /></p>
        <h6>Σημαντικές Διελεύσεις</h6>
        <ListGroup variant="flush">
          {yearlyForecast.transits.map((t, i) => (
            <ListGroup.Item key={i}>
              <HtmlContent content={t} />
            </ListGroup.Item>
          ))}
        </ListGroup>
      </SectionCard>

      <SectionCard title="Επαγγελματική Καθοδήγηση">
        <p>
          <strong>Ιδανικό Περιβάλλον:</strong> 
          <HtmlContent content={careerGuidance.idealEnvironment} />
        </p>
        <h6>Προτεινόμενοι Ρόλοι:</h6>
        <ListGroup>
          {careerGuidance.suggestedRoles.map((r, i) => (
            <ListGroup.Item key={i}>
              <HtmlContent content={r} />
            </ListGroup.Item>
          ))}
        </ListGroup>
        <p className="mt-3">
          <em><HtmlContent content={careerGuidance.finalSummary} /></em>
        </p>
      </SectionCard>
      year_prediction
      <SectionCard title="Ετήσια Πρόβλεψη">
        <p><HtmlContent content={yearPrediction} /></p>
      </SectionCard>
      <SectionCard title="Τελική Επισκόπηση">
        <p><HtmlContent content={finalOverview} /></p>
      </SectionCard>
    </Container>
  );
}

export default AstrologyResult;