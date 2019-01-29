package com.ctrip.platform.dal.dao.helper;

import com.ctrip.platform.dal.dao.annotation.Type;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EntityManagerTest {

    @Test
    public void testGrandParentClass() {
        try {
            EntityManager grand = EntityManager.getEntityManager(GrandParent.class);
            String[] columnNames = grand.getColumnNames();
            List<String> actualColumnNames = Arrays.asList(columnNames);
            Collections.sort(actualColumnNames);

            List<String> expectedColumnNames = getExpectedColumnNamesOfGrandParent();
            Collections.sort(expectedColumnNames);

            Assert.assertEquals(actualColumnNames, expectedColumnNames);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    private List<String> getExpectedColumnNamesOfGrandParent() {
        List<String> expectedNames = new ArrayList<>();
        expectedNames.add("grandParentId");
        expectedNames.add("grandParentName");
        return expectedNames;
    }

    @Test
    public void testParentClass() {
        try {
            EntityManager parent = EntityManager.getEntityManager(Parent.class);
            String[] columnNames = parent.getColumnNames();
            List<String> actualColumnNames = Arrays.asList(columnNames);
            Collections.sort(actualColumnNames);

            List<String> expectedColumnNames = getExpectedColumnNamesOfParent();
            Collections.sort(expectedColumnNames);

            Assert.assertEquals(actualColumnNames, expectedColumnNames);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    private List<String> getExpectedColumnNamesOfParent() {
        List<String> expectedNames = new ArrayList<>();
        expectedNames.addAll(getExpectedColumnNamesOfGrandParent());

        expectedNames.add("parentId");
        expectedNames.add("parentName");
        return expectedNames;
    }

    @Test
    public void testChildClass() {
        try {
            EntityManager child = EntityManager.getEntityManager(Child.class);
            String[] columnNames = child.getColumnNames();
            List<String> actualColumnNames = Arrays.asList(columnNames);
            Collections.sort(actualColumnNames);

            List<String> expectedColumnNames = getExpectedColumnNamesOfChild();
            Collections.sort(expectedColumnNames);

            Assert.assertEquals(actualColumnNames, expectedColumnNames);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    private List<String> getExpectedColumnNamesOfChild() {
        List<String> expectedNames = new ArrayList<>();
        expectedNames.addAll(getExpectedColumnNamesOfParent());

        expectedNames.add("childId");
        expectedNames.add("childName");
        return expectedNames;
    }

    private class GrandParent {
        @Id
        @Column(name = "grandParentId")
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Type(value = Types.INTEGER)
        private int grandParentId;

        @Column(name = "grandParentName")
        @Type(value = Types.VARCHAR)
        private String grandParentName;

        public int getGrandParentId() {
            return grandParentId;
        }

        public void setGrandParentId(int grandParentId) {
            this.grandParentId = grandParentId;
        }

        public String getGrandParentName() {
            return grandParentName;
        }

        public void setGrandParentName(String grandParentName) {
            this.grandParentName = grandParentName;
        }
    }

    private class Parent extends GrandParent {
        @Id
        @Column(name = "parentId")
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Type(value = Types.INTEGER)
        private int parentId;

        @Column(name = "parentName")
        @Type(value = Types.VARCHAR)
        private String parentName;

        public int getParentId() {
            return parentId;
        }

        public void setParentId(int parentId) {
            this.parentId = parentId;
        }

        public String getParentName() {
            return parentName;
        }

        public void setParentName(String parentName) {
            this.parentName = parentName;
        }
    }

    private class Child extends Parent {
        @Id
        @Column(name = "childId")
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Type(value = Types.INTEGER)
        private int childId;

        @Column(name = "childName")
        @Type(value = Types.VARCHAR)
        private String childName;

        public int getChildId() {
            return childId;
        }

        public void setChildId(int childId) {
            this.childId = childId;
        }

        public String getChildName() {
            return childName;
        }

        public void setChildName(String childName) {
            this.childName = childName;
        }
    }

}
