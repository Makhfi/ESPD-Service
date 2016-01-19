package eu.europa.ec.grow.espd.xml.response

import eu.europa.ec.grow.espd.domain.EconomicOperatorImpl
import eu.europa.ec.grow.espd.domain.EconomicOperatorRepresentative
import eu.europa.ec.grow.espd.xml.LocalDateAdapter
import eu.europa.ec.grow.espd.xml.base.AbstractEspdXmlMarshalling
import eu.europa.ec.grow.espd.constants.enums.Country
import eu.europa.ec.grow.espd.domain.EspdDocument
import eu.europa.ec.grow.espd.domain.PartyImpl
import org.joda.time.LocalDate

/**
 * Created by ratoico on 11/26/15.
 */
class EspdResponseMarshallingTest extends AbstractEspdXmlMarshalling {

    def "should make sure that we use the correct XML namespaces"() {
        when:
        def result = parseResponseXml()

        then:
        result.lookupNamespace('espd-req') == 'urn:grow:names:specification:ubl:schema:xsd:ESPDRequest-1'
        result.lookupNamespace('espd') == 'urn:grow:names:specification:ubl:schema:xsd:ESPDResponse-1'
        result.lookupNamespace('espd-cac') == 'urn:grow:names:specification:ubl:schema:xsd:ESPD-CommonAggregateComponents-1'
        result.lookupNamespace('espd-cbc') == 'urn:grow:names:specification:ubl:schema:xsd:ESPD-CommonBasicComponents-1'
        result.lookupNamespace('cbc') == 'urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2'
        result.lookupNamespace('cac') == 'urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2'
        result.lookupNamespace('ccv') == 'urn:isa:names:specification:ubl:schema:xsd:CCV-CommonAggregateComponents-1'
        result.lookupNamespace('ccv-cbc') == 'urn:isa:names:specification:ubl:schema:xsd:CCV-CommonBasicComponents-1'
        result.lookupNamespace('cev-cbc') == 'urn:isa:names:specification:ubl:schema:xsd:CEV-CommonBasicComponents-1'
    }

    def "should contain UBLVersionID element information"() {
        when:
        def result = parseResponseXml()

        then:
        result.UBLVersionID.text() == "2.1"
        result.UBLVersionID.@schemeAgencyID.text() == "OASIS-UBL-TC"
    }

    def "should contain CustomizationID element information"() {
        when:
        def result = parseResponseXml()

        then:
        result.CustomizationID.text() == "urn:www.cenbii.eu:transaction:biitrns092:ver3.0"
        result.CustomizationID.@schemeAgencyID.text() == "BII"
        result.CustomizationID.@schemeVersionID.text() == "3.0"
        result.CustomizationID.@schemeName.text() == "CustomizationID"
    }

    def "should contain ID element information"() {
        when:
        def result = parseResponseXml()

        then: "id is an UUID"
        result.ID.text().length() == 36

        then:
        result.ID.@schemeAgencyID.text() == "EU-COM-GROW"
        result.ID.@schemeAgencyName.text() == "DG GROW (European Commission)"
        result.ID.@schemeVersionID.text() == "1.1"
        result.ID.@schemeID.text() == "ISO/IEC 9834-8:2008 - 4UUID"
    }

    def "should contain CopyIndicator element information"() {
        when:
        def result = parseResponseXml()

        then:
        result.CopyIndicator.size() == 1
        result.CopyIndicator.toBoolean() == false
    }

    def "should contain VersionID element information"() {
        when:
        def result = parseResponseXml()

        then:
        result.VersionID.text() == "1"
        result.VersionID.@schemeAgencyID.text() == "EU-COM-GROW"
    }

    def "should contain IssueDate element information"() {
        when:
        def result = parseResponseXml()

        then: "issue date must match the date format YYYY-MM-dd"
        result.IssueDate.text() ==~ "\\d{4}-\\d{2}-\\d{2}"
    }

    def "should contain IssueTime element information"() {
        when:
        def result = parseResponseXml()

        then: "issue time must match the time format HH:mm:ss"
        result.IssueTime.text() ==~ "\\d{2}:\\d{2}:\\d{2}"
    }

    def "should contain ContractingParty element information"() {
        given:
        def authority = new PartyImpl(name: "  Hodor authority  ", vatNumber: "  Hodor national reg number  ",
                street: "  Hodor street  ", postalCode: "  Hodor postcode  ", city: "  Hodor city  ", country: Country.ROMANIA,
                contactName: "  Hodor contact person  ", contactEmail: "  hodor@hodor.com  ", contactPhone: "  555-HODOR  ",
                website: "  www.hodor.com  ")
        def espd = new EspdDocument(authority: authority)

        when:
        def result = parseResponseXml(espd)

        then: "all values should be trimmed"
        result.ContractingParty.Party.PartyName.Name.text() == "Hodor authority"
        result.ContractingParty.Party.WebsiteURI.text() == "www.hodor.com"

        then: "party identification"
        result.ContractingParty.Party.PartyIdentification.ID.text() == "Hodor national reg number"

        then: "check address information"
        result.ContractingParty.Party.PostalAddress.Country.IdentificationCode.text() == "RO"
        result.ContractingParty.Party.PostalAddress.Country.IdentificationCode.@listAgencyID.text() == "ISO"
        result.ContractingParty.Party.PostalAddress.Country.IdentificationCode.@listName.text() == "ISO 3166-1"
        result.ContractingParty.Party.PostalAddress.Country.IdentificationCode.@listVersionID.text() == "1.0"
        result.ContractingParty.Party.PostalAddress.CityName.text() == "Hodor city"
        result.ContractingParty.Party.PostalAddress.StreetName.text() == "Hodor street"
        result.ContractingParty.Party.PostalAddress.Postbox.text() == "Hodor postcode"

        then: "check contact information"
        result.ContractingParty.Party.Contact.Name.text() == "Hodor contact person"
        result.ContractingParty.Party.Contact.ElectronicMail.text() == "hodor@hodor.com"
        result.ContractingParty.Party.Contact.Telephone.text() == "555-HODOR"
    }

    def "should contain EconomicOperatorParty element information"() {
        given:
        def economicOperator = new EconomicOperatorImpl(name: "  ACME Corp.  ", vatNumber: "  B207781243  ", anotherNationalId: "B66666",
                street: "  Vitruvio  ", postalCode: "  28006  ", city: "  Madrid  ", country: Country.SPAIN,
                contactName: "  Hodor contact person  ", contactEmail: "  hodor@hodor.com  ", contactPhone: "  +34 96 123 456  ",
                website: "  www.hodor.com  ", isSmallSizedEnterprise: true)
        def espd = new EspdDocument(economicOperator: economicOperator)

        when:
        def result = parseResponseXml(espd)

        then: "all values should be trimmed"
        result.EconomicOperatorParty.Party.PartyName.Name.text() == "ACME Corp."
        result.EconomicOperatorParty.Party.WebsiteURI.text() == "www.hodor.com"

        then: "party identification"
        result.EconomicOperatorParty.Party.PartyIdentification.ID[0].text() == "B207781243"
        result.EconomicOperatorParty.Party.PartyIdentification.ID[1].text() == "B66666"

        then: "check address information"
        result.EconomicOperatorParty.Party.PostalAddress.Country.IdentificationCode.text() == "ES"
        result.EconomicOperatorParty.Party.PostalAddress.Country.IdentificationCode.@listAgencyID.text() == "ISO"
        result.EconomicOperatorParty.Party.PostalAddress.Country.IdentificationCode.@listName.text() == "ISO 3166-1"
        result.EconomicOperatorParty.Party.PostalAddress.Country.IdentificationCode.@listVersionID.text() == "1.0"
        result.EconomicOperatorParty.Party.PostalAddress.CityName.text() == "Madrid"
        result.EconomicOperatorParty.Party.PostalAddress.StreetName.text() == "Vitruvio"
        result.EconomicOperatorParty.Party.PostalAddress.Postbox.text() == "28006"

        then: "check contact information"
        result.EconomicOperatorParty.Party.Contact.Name.text() == "Hodor contact person"
        result.EconomicOperatorParty.Party.Contact.ElectronicMail.text() == "hodor@hodor.com"
        result.EconomicOperatorParty.Party.Contact.Telephone.text() == "+34 96 123 456"

        then: "other"
        result.EconomicOperatorParty.SMEIndicator.text() == "true"
    }

    def "should contain RepresentativeNaturalPerson element information"() {
        given:
        def birthDate = new Date()
        def economicOperator = new EconomicOperatorImpl(representative: new EconomicOperatorRepresentative(
                firstName: "Emilio", lastName: "García De Tres Torres", dateOfBirth: birthDate,
                placeOfBirth: "València, Spain", street: "Vitruvio", postalCode: "28006", city: "Madrid",
                country: Country.SPAIN, email: "emilio.garcia3torres@acme.com", phone: "+34 96 123 456",
                position: "Empowered to represent the Consortium",
                additionalInfo: "Can represent ACME, Corp. and the Consortia to which ACME, Corp"))
        def espd = new EspdDocument(economicOperator: economicOperator)

        when:
        def result = parseResponseXml(espd)

        then: "person details"
        result.EconomicOperatorParty.RepresentativeNaturalPerson.PowerOfAttorney.AgentParty.Person.FirstName.text() == "Emilio"
        result.EconomicOperatorParty.RepresentativeNaturalPerson.PowerOfAttorney.AgentParty.Person.FamilyName.text() == "García De Tres Torres"
        result.EconomicOperatorParty.RepresentativeNaturalPerson.PowerOfAttorney.AgentParty.Person.BirthDate.text() == LocalDateAdapter.marshal(new LocalDate(birthDate))
        result.EconomicOperatorParty.RepresentativeNaturalPerson.PowerOfAttorney.AgentParty.Person.BirthplaceName.text() == "València, Spain"

        then: "check address information"
        result.EconomicOperatorParty.RepresentativeNaturalPerson.PowerOfAttorney.AgentParty.Person.ResidenceAddress.Country.IdentificationCode.text() == "ES"
        result.EconomicOperatorParty.RepresentativeNaturalPerson.PowerOfAttorney.AgentParty.Person.ResidenceAddress.CityName.text() == "Madrid"
        result.EconomicOperatorParty.RepresentativeNaturalPerson.PowerOfAttorney.AgentParty.Person.ResidenceAddress.StreetName.text() == "Vitruvio"
        result.EconomicOperatorParty.RepresentativeNaturalPerson.PowerOfAttorney.AgentParty.Person.ResidenceAddress.Postbox.text() == "28006"

        then: "check contact information"
        result.EconomicOperatorParty.RepresentativeNaturalPerson.PowerOfAttorney.AgentParty.Person.Contact.ElectronicMail.text() == "emilio.garcia3torres@acme.com"
        result.EconomicOperatorParty.RepresentativeNaturalPerson.PowerOfAttorney.AgentParty.Person.Contact.Telephone.text() == "+34 96 123 456"

        then: "other"
        result.EconomicOperatorParty.RepresentativeNaturalPerson.NaturalPersonRoleDescription.text() == "Empowered to represent the Consortium"
        result.EconomicOperatorParty.RepresentativeNaturalPerson.PowerOfAttorney.Description.text() == "Can represent ACME, Corp. and the Consortia to which ACME, Corp"
    }

    def "should contain ProcurementProjectLot element information when there are no lots"() {
        when:
        def result = parseResponseXml()

        then: "In a Procurement Project with no Lots one ProcurementProjectLot element, and only one, MUST be included in the XML instance"
        result.ProcurementProjectLot.size() == 1

        then: "The identifier for this single ProcurementProjectLot MUST be the number 0"
        result.ProcurementProjectLot.ID.text() == "0"
    }

    def "should contain AdditionalDocumentReference element information"() {
        given:
        def espd = new EspdDocument(ojsNumber: "S206|2015-10-23|PN33|2015/S 206-373035",
                procedureTitle: "Belgium-Brussels: SMART 2015/0065 — Benchmarking deployment of eHealth among general practitioners 2015",
                procedureShortDesc: "Service category No 11: Management consulting services [6] and related services."
        )

        when:
        def result = parseResponseXml(espd)

        then:
        result.AdditionalDocumentReference[0].ID.text() == "S206|2015-10-23|PN33|2015/S 206-373035"
        result.AdditionalDocumentReference[0].ID.@schemeID.text() == "ISO/IEC 9834-8:2008 - 4UUID"
        result.AdditionalDocumentReference[0].ID.@schemeAgencyID.text() == "EU-COM-GROW"
        result.AdditionalDocumentReference[0].ID.@schemeAgencyName.text() == "DG GROW (European Commission)"
        result.AdditionalDocumentReference[0].ID.@schemeVersionID.text() == "1.1"

        then:
        result.AdditionalDocumentReference[0].DocumentTypeCode.@listAgencyID.text() == "EU-COM-GROW"
        result.AdditionalDocumentReference[0].DocumentTypeCode.@listID.text() == "ReferencesTypeCodes"
        result.AdditionalDocumentReference[0].DocumentTypeCode.@listVersionID.text() == "1.0"
        result.AdditionalDocumentReference[0].DocumentTypeCode.text() == "TeD_CN"

        then:
        result.AdditionalDocumentReference[0].Attachment.ExternalReference.FileName.text() == "Belgium-Brussels: SMART 2015/0065 — Benchmarking deployment of eHealth among general practitioners 2015"
        result.AdditionalDocumentReference[0].Attachment.ExternalReference.Description[0].text() == "Service category No 11: Management consulting services [6] and related services."
    }

    def "should contain ContractFolderID element information"() {
        given:
        def espd = new EspdDocument(fileRefByCA: "HODOR refd by CA")

        when:
        def result = parseResponseXml(espd)

        then:
        result.ContractFolderID.text() == "HODOR refd by CA"
        result.ContractFolderID.@schemeAgencyID.text() == "TeD"
    }

}