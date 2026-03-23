package com.bytedesk.starter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class MeetRoomInviteUidChangeLogTest {

    @Test
    void masterIncludesNullableFollowUpMigration() throws Exception {
        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("db/changelog/master.xml")) {
            assertNotNull(inputStream);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            Document document = factory.newDocumentBuilder().parse(inputStream);

            NodeList includes = document.getElementsByTagName("include");
            boolean found = false;
            for (int index = 0; index < includes.getLength(); index++) {
                Element include = (Element) includes.item(index);
                if ("db/changelog/migration/260323_drop_meet_room_invite_uid_not_null.xml"
                        .equals(include.getAttribute("file"))) {
                    found = true;
                    break;
                }
            }

            assertTrue(found);
        }
    }

    @Test
    void notNullChangeSetMarksRanWhenMeetRoomTableIsAbsent() throws Exception {
        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("db/changelog/migration/260316_add_meet_room_invite_uid.xml")) {
            assertNotNull(inputStream);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            Document document = factory.newDocumentBuilder().parse(inputStream);

            Element changeSet = findChangeSet(document, "260316-add-meet-room-invite-uid-not-null");
            Element validCheckSum = firstChild(changeSet, "validCheckSum");
            Element preConditions = firstChild(changeSet, "preConditions");
            Element andElement = firstChild(preConditions, "and");
            Element tableExists = firstChild(andElement, "tableExists");
            Element columnExists = firstChild(andElement, "columnExists");
            Element sqlCheck = firstChild(andElement, "sqlCheck");

            assertEquals("ANY", validCheckSum.getTextContent().trim());
            assertEquals("MARK_RAN", preConditions.getAttribute("onFail"));
            assertEquals("MARK_RAN", preConditions.getAttribute("onError"));
            assertEquals("bytedesk_meet_room", tableExists.getAttribute("tableName"));
            assertEquals("bytedesk_meet_room", columnExists.getAttribute("tableName"));
            assertEquals("invite_uid", columnExists.getAttribute("columnName"));
            assertEquals("0", sqlCheck.getAttribute("expectedResult"));
        }
    }

    private Element findChangeSet(Document document, String id) {
        NodeList changeSets = document.getElementsByTagName("changeSet");
        for (int index = 0; index < changeSets.getLength(); index++) {
            Element candidate = (Element) changeSets.item(index);
            if (id.equals(candidate.getAttribute("id"))) {
                return candidate;
            }
        }
        throw new IllegalArgumentException("Missing changeSet: " + id);
    }

    private Element firstChild(Element parent, String tagName) {
        NodeList childNodes = parent.getChildNodes();
        for (int index = 0; index < childNodes.getLength(); index++) {
            Node node = childNodes.item(index);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element element = (Element) node;
            if (tagName.equals(element.getTagName())) {
                return element;
            }
        }
        throw new IllegalArgumentException("Missing child tag: " + tagName);
    }
}